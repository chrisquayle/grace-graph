package com.chrisq.grace.graph.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.chrisq.grace.graph.AbstractGraph;
import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.Node;
import com.chrisq.grace.graph.context.data.DataGraph;
import com.chrisq.grace.graph.context.form.field.Field;
import com.chrisq.grace.graph.resource.ResourceScope;
import com.chrisq.grace.graph.resource.TemporalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class ContextGraph extends AbstractGraph {
    private static final Logger LOG = LoggerFactory.getLogger(ContextGraph.class);
    @JsonIgnore
    private ContextResourceId componentResourceId;
    @JsonIgnore
    private ContextResourceId baseResource;
    private Map<ContextResourceId, DataGraph> dataGraphs = new HashMap<>();
    private String contentKeyFieldId = "";
    private String contentDomainKeyFieldId = "";
    private String contextRefFieldId;
    private String shortId;

    protected ContextGraph() {
        super();
    }

    public ContextGraph(String id) {
        this(new ContextResourceId(id));
    }

    public ContextGraph(ContextResourceId resourceId) {
        super(resourceId.getId());
        this.setId(resourceId.getId());
    }

    public ContextGraph(TemporalResource fromResource) {
        super(fromResource);
    }

    @Override
    public void setId(String resourceId) {
        // if no error then the id is well formed
        super.setId(resourceId);

        // The inheritance model for context is as follows
        //      base = base (global) version
        //          |_ component = jurisdiction base
        //                  |_ individual ID = jurisdiction form

        // The form inherits base nodes (globals)
        baseResource = new ContextResourceId(resourceId);
        baseResource.setScope(ResourceScope.RESOURCE_SCOPE_DEFAULT);
        // Base form has no individual part (it is only for extending - so its 'abstract')
        baseResource.setIndividualIdPart(null);
        baseResource.setScope(ResourceScope.RESOURCE_SCOPE_DEFAULT);

        // We also need to inherit component level nodes
        componentResourceId = new ContextResourceId(resourceId);
        // Component form also has no individual part (it is only for extending - so its 'abstract')
        componentResourceId.setIndividualIdPart(null);
    }

    @Override
    public Link getLinkByTargetId(String targetId) {
        ContextResourceId resourceId = new ContextResourceId(targetId);
        Link link = super.getLinkByTargetId(targetId);
        if(link == null && !resourceId.isBaseResource()){
            // No specialised version found - try for base version
            link = super.getLinkByTargetId(resourceId.getBaseId());
        }
        return link;
    }

    @Override
    public Link getLinkBySourceId(String sourceId) {
        ContextResourceId resourceId = new ContextResourceId(sourceId);
        Link link = super.getLinkBySourceId(sourceId);
        if(link == null && !resourceId.isBaseResource()){
            // No specialised version found - try for base version
            link = super.getLinkBySourceId(resourceId.getBaseId());
        }
        return link;
    }

    @Override
    public Node getNode(String nodeId) {
        ContextResourceId baseNode = new ContextResourceId(nodeId);
        if (!this.getNodes().containsKey(nodeId)) {
            return this.getNodes().get(baseNode.getBaseId());
        }
        return super.getNode(nodeId);
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getContextRefFieldId() {
        return contextRefFieldId;
    }

    public void setContextRefFieldId(String contextRefFieldId) {
        this.contextRefFieldId = contextRefFieldId;
    }

    public Field getContentKeyField() {
        Optional<Node> result = this.getNodes().values().stream().filter(f -> f.getId().equalsIgnoreCase(contentKeyFieldId)).findFirst();
        return (Field) result.orElse(null);
    }

    public String getContentKeyFieldId() {
        return contentKeyFieldId;
    }

    public String getContentDomainKeyFieldId() {
        return contentDomainKeyFieldId;
    }

    public void setContentDomainKeyFieldId(String contentDomainKeyFieldId) {
        this.contentDomainKeyFieldId = contentDomainKeyFieldId;
    }

    public void setContentKeyFieldId(String contentKeyFieldId) {
        this.contentKeyFieldId = contentKeyFieldId;
    }

    public ContextResourceId getComponentResourceId() {
        return componentResourceId;
    }

    @JsonIgnore
    public ContextResourceId getBaseResourceId() {
        return baseResource;
    }

    @Override
    public Link getLink(String key) {
        for (Link link : this.getLinks().values()) {
            ContextLink clink = new ContextLink((Field)link.getSource(), (Field)link.getTarget());
            if (clink.isInContext(key)) {
                return clink;
            }
        }
        return null;
    }

    public Map<ContextResourceId, DataGraph> getDataGraphs() {
        return dataGraphs;
    }

    public void setDataGraphs(Map<ContextResourceId, DataGraph> dataGraphs) {
        this.dataGraphs = dataGraphs;
    }
}
