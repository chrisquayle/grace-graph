package com.chrisq.grace.graph.context.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrisq.grace.graph.Graph;
import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.Node;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.ContextHeaders;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.context.form.field.*;
import com.chrisq.grace.graph.resource.ResourceException;
import com.chrisq.grace.graph.resource.ResourceType;
import com.chrisq.grace.graph.util.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ContextForm extends AbstractField {
    private static final Logger LOG = LoggerFactory.getLogger(ContextForm.class);

    private UUID versionId;

    // Single static object mapper for basic serialization
    private static ObjectMapper objectMapper = Mapper.getObjectMapper();
    private ContextGraph contextGraph;
    private boolean searchForm = false;

    protected ContextForm() {
        super();
    }

    public ContextForm(ContextGraph contextGraph) throws ResourceException {
        super(new ContextResourceId(contextGraph.getId()), NodeType.OBJECT);
        this.contextGraph = contextGraph;
    }

    @Override
    public UUID getVersionId() {
        return versionId;
    }

    @Override
    public void setVersionId(UUID versionId) {
        this.versionId = versionId;
    }

    @JsonIgnore
    @Override
    public String toSchemaJson() {
        Map<String, Object> jsonSchemaMap = toSchemaMap();
        try {
            return objectMapper.writeValueAsString(jsonSchemaMap);
        } catch (JsonProcessingException e) {
            throw new TouchContextException("JSON Processing error", e);
        }
    }

    @JsonIgnore
    public String toSchemaJsonPretty() {
        Map<String, Object> jsonSchemaMap = toSchemaMap();
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchemaMap);
        } catch (JsonProcessingException e) {
            throw new TouchContextException("JSON Processing error", e);
        }
    }

    @JsonIgnore
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> jsonSchemaMap = new LinkedHashMap<String, Object>();
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        jsonSchemaMap.put("$schema", "http://json-schema.org/draft-04/schema#");
        jsonSchemaMap.put("id", this.getId());
        jsonSchemaMap.put("type", NodeType.OBJECT.getSchemaType());
        jsonSchemaMap.put("title", this.getTitle());
        jsonSchemaMap.put("description", this.getDescription());
        jsonSchemaMap.put("properties", properties);
        if (contextGraph.getRefGraphs().size() > 0) {
            Map<String, Object> definitions = new LinkedHashMap<>();
            jsonSchemaMap.put("definitions", definitions);
            for (Graph refGraph : contextGraph.getRefGraphs().values()) {
                ContextGraph ctxGraph = (ContextGraph) refGraph;
                ContextForm form = new ContextForm(ctxGraph);
                definitions.put(refGraph.getId(), form.toSchemaMap());
            }
        }
        for (Link rootLink : contextGraph.getRootLinks().values()) {
            if (rootLink.getSource().getId().equalsIgnoreCase(this.getId())
                    || rootLink.getSource().getId().equalsIgnoreCase(contextGraph.getBaseResourceId().getId())
                    || rootLink.getSource().getId().equalsIgnoreCase(contextGraph.getComponentResourceId().getId())) {
                // Direct properties of the root node are loaded from the target field
                buildSchema(properties, rootLink.getTarget());
            } else {
                // Root objects (Child nodes) are loaded from the source field
                buildSchema(properties, rootLink.getSource());
            }
        }
        this.addContextHeaders(properties);
        return jsonSchemaMap;
    }

    private void buildSchema(Map<String, Object> map, Node node) {
        if(this.searchForm && node instanceof Field){
            if(!((Field)node).isSearchable()) {
                // Only searchable fields can be included in search forms
                return;
            } else {
                // Searchable fields are all optional
                ((Field)node).setRequired(false);
            }
        }
        Map<String, Object> nodeMap = node.toSchemaMap();
        map.put(node.getId(), nodeMap);
        // Recurse children
        Map<String, Link> targetLinks = contextGraph.getLinks(node);
        if (targetLinks.size() > 0) {
            Map<String, Object> properties = new LinkedHashMap<>();
            if (node.getNodeType() == NodeType.CONTEXT_ARRAY) {
                Map<String, Object> itemsNode = new LinkedHashMap<>();
                // Array nodes should only have one child - this is the array object or value definition
                Map.Entry<String, Link> entry = targetLinks.entrySet().iterator().next();
                Link targetLink = entry.getValue();
                if (targetLink == null) {
                    throw new TouchContextException("Array property not found. Array nodes are expected to have one child. " + node.getId());
                }
                Node itemsField = targetLink.getTarget();
                Map<String, Object> itemsMap = itemsField.toSchemaMap();
                itemsMap.remove("title");
                nodeMap.put("items", itemsMap);
                targetLinks = contextGraph.getLinks(itemsField);
            }
            for (Link link : targetLinks.values()) {
                // Recurse
                buildSchema(properties, link.getTarget());
            }
        }
    }


    @Override
    public String toOptionJs() {
        Map<String, Object> optionMap = this.toOptionMap();
        try {
            // Script tags have been added to form options so that we can transform to script rather than valid JSON
            String json = Mapper.getObjectMapper().writeValueAsString(optionMap);
            json = json.replaceAll(".<script>", "");
            json = json.replaceAll("</script>.", "");
            return json;
        } catch (JsonProcessingException e) {
            throw new TouchContextException("JSON options Processing error", e);
        }
    }

    // Trim off the outside brackets so that the fragment can be dropped into a JS script or json block
    // Expect this to be called via AJAX
    public String toOptionJsFragment() {
        String json = this.toOptionJs();
        json = json.substring(1, json.length());
        json = json.substring(0, json.length() - 1);
        return json;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> toOptionMap() {
        Map<String, Object> optionMap = new LinkedHashMap<>();

//        //add form options here - if we need edit buttons and stuff
//        LinkedHashMap<String, Object> formMap = putNewMap(optionMap, "form");
//        Map<String, Object> buttonMap = putNewMap(formMap, "buttons");
//        Map<String, Object> editMap = putNewMap(buttonMap, "edit");
//        editMap.put("title", "Edit");

        Map<String, Object> fieldOptionMap = toFieldOptionMap();
        //optionMap.put("fields", fieldOptionMap);
        return fieldOptionMap;
    }

    private Map<String, Object> toFieldOptionMap() {

        Map<String, Object> fieldMap = new LinkedHashMap<String, Object>();

        for (Link rootLink : contextGraph.getRootLinks().values()) {
            if (rootLink.getSource().getId().equalsIgnoreCase(this.getId())
                    || rootLink.getSource().getId().equalsIgnoreCase(contextGraph.getBaseResourceId().getId())
                    || rootLink.getSource().getId().equalsIgnoreCase(contextGraph.getComponentResourceId().getId())) {
                // Direct properties of the root node are loaded from the target field
                buildOptions(fieldMap, (Field) rootLink.getTarget());
            } else {
                // Root objects (Child nodes) are loaded from the source field
                buildOptions(fieldMap, (Field) rootLink.getSource());
            }
        }
        this.addContextHeaderOptions(fieldMap);
        return fieldMap;
    }


    private void addContextHeaders(Map<String, Object> map) {
        // Add context header schema for correlation IDs. Content based on this base will be able to store IDs against this schema
        AbstractField contextHeaders = new ObjectField(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId());

        AbstractField contextId = new TextField(ContextHeaders.FieldEnum.CONTEXT_ID.getContextId());
        contextId.setDefaultValue(this.getId());

        AbstractField contentId = new TextField(ContextHeaders.FieldEnum.CONTENT_ID.getContextId());
        if (contextGraph.getContentKeyField() == null) {
            LOG.warn("ContentKeyField is not provided for Context Graph {}. Content ID header cannot be set.", this.getId());
        } else {
            contentId.setDefaultValue(contextGraph.getContentKeyField().getId());
            // Set the default value for the form ID field to be the form ID. The persistence logic will look for this placeholder and replace it with an instance ID for the content
            contextGraph.getContentKeyField().setDefaultValue(this.getId());

        }

        Map<String, Object> headersMap = contextHeaders.toSchemaMap();
        map.put(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId(), headersMap);

        Map<String, Object> propMap = new LinkedHashMap<>();
        headersMap.put("properties", propMap);

        propMap.put(contextId.getId(), contextId.toSchemaMap());
        propMap.put(contentId.getId(), contentId.toSchemaMap());
    }

    private void addContextHeaderOptions(Map<String, Object> map) {
        Map<String, Object> headersMap = new LinkedHashMap<>();
        map.put(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId(), headersMap);
        headersMap.put("hidden", true);
    }

    private void buildOptions(Map<String, Object> map, Field field) {
        Map<String, Object> nodeMap = field.toOptionMap();
        map.put(field.getId(), nodeMap);
        // Recurse children
        Map<String, Link> targetLinks = contextGraph.getLinks(field);
        int propCount = targetLinks.size();
        if (propCount > 0) {
            Map<String, Object> fields = new LinkedHashMap<>();
            if (field.getNodeType() == NodeType.CONTEXT_ARRAY) {
                Map<String, Object> itemsNode = new LinkedHashMap<>();
                // Array nodes should only have one child - this is the array object or value definition
                Map.Entry<String, Link> entry = targetLinks.entrySet().iterator().next();
                Field itemsField = (Field) entry.getValue().getTarget();
                Map<String, Object> itemsMap = itemsField.toOptionMap();
                nodeMap.put("items", itemsMap);
                //itemsMap.put("fields", fields);
                targetLinks = contextGraph.getLinks(itemsField);
                ContextGraph refGraph = (ContextGraph) contextGraph.getRefGraphById(itemsField.getId());
                ContextForm form = new ContextForm(refGraph);
                itemsMap.put("fields", form.toOptionMap());

            }
            for (Map.Entry<String, Link> link : targetLinks.entrySet()) {
                Field target = (Field) link.getValue().getTarget();
                // Recurse
                buildOptions(fields, target);
            }
        }
    }

    // Compile some front-end listener wire-ups for any listable controls
    // This will probably be called via AJAX - so ignore any 'not used' inspection message
    public String generateRenderFunctions() {
        // Subscribe listable observers
        Map<String, String> bindingPaths = new HashMap<>();

        StringBuilder functionBuilder = new StringBuilder();
        collectBindingPaths(bindingPaths, contextGraph, "/");
        for(Graph refGraph : contextGraph.getRefGraphs().values()) {
            ContextResourceId sourceRef = refGraph.getContextResourceId();
            sourceRef.setResourceType(ResourceType.CONTEXT_FIELD);
            collectBindingPaths(bindingPaths, refGraph, "/" + sourceRef.getBaseId() + "[0]/");
        }
        for(Map.Entry<String, String> binding : bindingPaths.entrySet()){
            // See http://www.alpacajs.org/docs/api/observables.html
            functionBuilder.append("addDataListSubscription(control, \"" + binding.getKey() + "\", \"" + binding.getValue() + "\");\n");
        }
        return functionBuilder.toString();
    }

    private void collectBindingPaths(Map bindingPaths, Graph contextGraph, String path){
        // TODO: For cascading lists , here we can only subscribe fields to listen for new filters from fields in the same graph.
        //       We will eventually want to link fields in different sub forms (ref-graphs), so the path construction will need to be smarter
        for (Link link : contextGraph.getLinks().values()) {
            if (link.getTarget() instanceof ListableField) {
                // Alpaca has an observable API that needs a cardinal path to locate control instances.
                ListableField subscriber = (ListableField) link.getTarget();
                if (subscriber.getDataListFilterPath() != null) {
                    String publisherPath = path + subscriber.getDataListFilterPath();
                    String subscriberPath = path + subscriber.getId();
                    bindingPaths.put(publisherPath, subscriberPath);
                }
            }
        }
    }

    public ContextGraph getContext() {
        return contextGraph;
    }

    /***
     * Create a basic JSON meta-schema for this field. This is to support form designer tools
     * @return
     */
    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> schemaRoot = new LinkedHashMap<>();
        schemaRoot.put("$schema", "http://json-schema.org/draft-04/schema#");
        schemaRoot.put("id", this.getId());
        schemaRoot.put("type", NodeType.OBJECT.getSchemaType());
        schemaRoot.put("title", this.getTitle());
        schemaRoot.put("description", this.getDescription());

        Map<String, Object> properties = new LinkedHashMap<>();
        schemaRoot.put("properties", properties);
        LinkedHashMap<String, Object> idMap = putNewMetaPropertyMap(properties, "id", NodeType.TEXT_STRING, this.getId(), true, true);
        putNewMetaPropertyMap(properties, "title", NodeType.TEXT_STRING, this.getTitle(), true);
        putNewMetaPropertyMap(properties, "description", NodeType.TEXT_STRING, this.getDescription(), false);
        return schemaRoot;
    }

    @Override
    public Map<String, Object> toMetaOptionMap() {
        Map<String, Object> optionRoot = new LinkedHashMap<>();
        Map<String, Object> fields = putNewMap(optionRoot, "fields");
        return optionRoot;
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> dataRoot = new LinkedHashMap<>();
        dataRoot.put("id", this.getId());
        dataRoot.put("title", this.getTitle());
        dataRoot.put("description", this.getDescription());
        return dataRoot;
    }

    @Override
    public String toMetaSchemaJson() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaSchemaMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta schema JSON. " + e.getMessage(), e);
        }
    }

    @Override
    public String toMetaOptionJs() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaOptionMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta option JSON. " + e.getMessage(), e);
        }
    }

    @Override
    public String toMetaDataJson() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaDataMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta data JSON. " + e.getMessage(), e);
        }
    }

    public boolean isSearchForm() {
        return searchForm;
    }

    public void setSearchForm(boolean searchForm) {
        this.searchForm = searchForm;
    }
}
