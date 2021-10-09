package com.chrisq.grace.graph.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrisq.grace.graph.*;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.ContextHeaders;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.context.data.DataResource;
import com.chrisq.grace.graph.context.form.field.ContextArray;
import com.chrisq.grace.graph.context.form.field.ContextReference;
import com.chrisq.grace.graph.context.form.field.Field;
import com.chrisq.grace.graph.context.form.field.ListableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class ContentGraph extends AbstractGraph {
    private static final Logger LOG = LoggerFactory.getLogger(ContentGraph.class);
    private GraphProvider contextGraphProvider;
    private ContextGraph contextGraph = null;
    private ContextResourceId contextGraphId = null;
    private String contextDomainKeyFieldId = null;
    private String contextDomainKey = null;
    private String scope;
    public UUID lastUpdateId = null;

    public ContentGraph() {
        super();
    }

    public ContentGraph(GraphProvider contextGraphProvider, String scope, ContextResourceId contextId, String graphId) {
        super(graphId);
        this.scope = (scope == null || scope.isEmpty()) ? this.scope : scope;
        this.contextGraphId = contextId;
        this.contextGraphProvider = contextGraphProvider;
        this.mapContext();
    }

    @Override
    public LinkedHashMap<String, Link> getLinks() {
        LinkedHashMap<String, Link> contentLinks = new LinkedHashMap<>();
        if (this.getId() != null && !this.getId().equalsIgnoreCase(this.getContext().getId())) {
            // The ID is not the context ID (Placeholder) so we need to make sure that the content links have been keyed correctly with sourceid#versionid/targetid#versionid in the link URN
            LinkedList<Link> sortedLinks = super.getLinks().values().stream()
                    .sorted(Comparator.comparing(Link::getId).thenComparing(Link::getSortIndex))
                    .collect(Collectors.toCollection(LinkedList::new));
            for (Link link : sortedLinks) {
                // Incoming links use the graph context ID as a placeholder link ID
                String newLinkId;
                ContentLink contentLink = (ContentLink) link;
                ContentNode contentNode = (ContentNode) contentLink.getSource();
                ContentNode targetNode = (ContentNode)contentLink.getTarget();
                if (contentNode.getId().equalsIgnoreCase(contentNode.getContextId())) {
                    // The link has not been updated yet to include the content ID
                    // TODO: Centralise ID generation functions. These need to be kept together and documented
                    String sourceId = contentNode.getId() + "#" + this.getId().toLowerCase();
                    String targetId = contentLink.getTargetId();

                    if(targetNode.getContextField().getNodeType().equals(NodeType.CONTEXT)){
                        // This is a ref graph instance so key it against the ref graph content ID - array members need to be unique
                        targetId = contentNode.getId() + "#" + ((String)targetNode.getValue()).toLowerCase();
                    }
                    contentLink.setId(sourceId + "/" + targetId);
                }
                contentLinks.put(contentLink.getId(), contentLink);
            }
            return contentLinks;
        }
        return super.getLinks();
    }

    @Override
    public LinkedHashMap<String, Link> getRootLinks() {
        LinkedHashMap<String, Link> rootLinks = new LinkedHashMap<>();
        for (Link link : this.getLinks().values()) {
            ContentNode contentNode = (ContentNode) link.getSource();
            if (contentNode.getContextField().getContextResourceId().resourceMatches(this.getContext().getId())) {
                rootLinks.put(link.getId(), link);
            }
        }
        return rootLinks;
    }

    public ContentNode getRootNode() {
        if (this.getId() == null || this.getContext() == null) return null;
        for (Node node : this.getSourceNodes().values()) {
            ContentNode contentNode = (ContentNode) node;
            if (contentNode.getContextField().getContextResourceId().resourceMatches(this.getContext().getId())) {
                return contentNode;
            }
        }
        return null;
    }

    public static ContentGraph fromMap(GraphProvider contextGraphProvider, String scope, Locale locale, Map<String, Object> map) throws TouchContextException {
        // Extract the context and content correlation IDs from the content headers section of the data
        ContextResourceId contextGraphId = getContextIdFromMap(map);
        ContextResourceId contentIdRef = getContentIdFromMap(map);

        // The content key ID tells us how to key the data
        String contentId;
        try {
            contentId = (String) map.get(contentIdRef.getId());
        } catch (RuntimeException e) {
            throw new TouchContextException(String.join(
                    "", "Content ID could not be set for base ", contextGraphId.getId(), " using field ref ", contentIdRef.getId()));
        }
        if (contentId == null || contentId.equalsIgnoreCase("")) {
            LOG.debug(String.join("Content ID is not set for base ", contextGraphId.getId(), " using field ref ", contentId, " the form will be treated as a new entry."));
        }
        LOG.debug("Content ID set: {}", contentId);

        // Build the form
        ContentGraph contentGraph = new ContentGraph(contextGraphProvider, scope, contextGraphId, contentId);
        // Now that we have a context ID, we can find the context base for this data
        contentGraph.mapContext();

        // Traverse the map and build all the graph links
        for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
            if (!mapEntry.getKey().equalsIgnoreCase(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId())) {
                Field rootContext = (Field) contentGraph.getContext().getNode(mapEntry.getKey());
                if (rootContext != null) { // Ignore elements which do not have a corresponding context node
                    ContentNode rootSource = new ContentNode((Field) contentGraph.getContext().getNode(contextGraphId.getId()), (NodeType.OBJECT.toString()));
                    ContentNode rootNode = new ContentNode(rootContext, mapEntry.getValue());
                    ContentLink rootLink = new ContentLink(contentGraph.getId(), rootSource, rootNode, 0);
                    contentGraph.buildGraph(rootLink);
                }
            }
        }
        contentGraph.bindContext();
        LOG.debug("Content base {} generated from JSON data ", contentGraph.getId());
        return contentGraph;
    }

    // Update new or changed properties in this content graph. Returns true if changes were made
    public boolean merge(ContentGraph mergeGraph) {
        // If this is a top-level (touchpoint/container) graph then we don't track versions. Look for the sub-graph signature e.g. JUR123-C1
        if (!this.getId().contains("-")) return false;
        boolean updated = false;
        String beforeValue;
        String afterValue;
        for (Link link : mergeGraph.getLinks().values()) {
            ContentLink incomingLink = (ContentLink) link;
            Link existingLink = super.getLinks().get(incomingLink.getId());
            if (existingLink != null) {
                beforeValue = ((ContentLink) existingLink).getValueAsString();
                afterValue = incomingLink.getValueAsString();
                if (!beforeValue.equalsIgnoreCase(afterValue)) {
                    if (this.getGraphSharingType() == GraphSharingType.SHARED_GRAPH && incomingLink.getTarget().getId().equalsIgnoreCase(this.getContextDomainKeyFieldId())) {
                        // This is an attempt to change the domain key - we can't allow it because it would orphan any existing parent graphs
                        throw new TouchContentException("Domain key update no allowed. " + incomingLink.getTarget().getTitle() + " (" + incomingLink.getTarget().getId());
                    }
                    // This is a new version of the link (the value has changed)
                    ContentNode target = (ContentNode) existingLink.getTarget();
                    // Add a new version of the link
                    existingLink.setVersionId(UUID.randomUUID());
                    existingLink.setCreatedUtc(Instant.now());
                    super.addGraphVersion(this.getParentGraphId(), existingLink.getCreatedUtc());
                    // Set the new value
                    target.setValue(afterValue);
                    updated = true;
                    LOG.info("Content Graph property updated: " + existingLink.getId() + ": " + target.getValue());
                }
            } else {
                // This is a new property.
                // Add the new link
                super.addLink(incomingLink);
                super.addGraphVersion(this.getParentGraphId(), incomingLink.getCreatedUtc());
                updated = true;
                LOG.debug("Content Graph property added: " + incomingLink.getId() + " = " + incomingLink.getValueAsString());
            }
        }
        return updated;
    }

    // Find the context using the injected context base provider
    private void mapContext() {
        if (this.contextGraph != null) return;
        if (this.contextGraphId == null) {
            LOG.debug("Set Context was called but there is no Context ID set - returning with no action");
            return;
        }
        Object graph = this.contextGraphProvider.getGraph(this.contextGraphId.getId(), null);
        if (graph == null) {
            throw new TouchContextException(String.join("",
                    "The Context ID could not be found in the found in the provided context directory. ", this.contextGraphId.getId(),
                    " does not match any available context base"));
        }
        this.contextGraph = (ContextGraph) graph;
        LOG.debug("Context base mapped: {}", this.contextGraphId);
    }

    // Find the context ID in the map
    public static ContextResourceId getContextIdFromMap(Map<String, Object> map) throws TouchContextException {
        Object id = getContextHeadersFromMap(map).get(ContextHeaders.FieldEnum.CONTEXT_ID.getContextId().getId());
        return new ContextResourceId((String) id);
    }

    // Find the content ID in the map
    public static ContextResourceId getContentIdFromMap(Map<String, Object> map) throws TouchContextException {
        Object id = getContextHeadersFromMap(map).get(ContextHeaders.FieldEnum.CONTENT_ID.getContextId().getId());
        return new ContextResourceId((String) id);
    }

    // Get the context headers section from the map
    public static Map<String, Object> getContextHeadersFromMap(Map<String, Object> map) throws TouchContextException {
        Map<String, Object> contextHeaders;
        try {
            contextHeaders = (Map<String, Object>) map.get(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId());
        } catch (NullPointerException e) {
            throw new TouchContextException(String.join("",
                    "Context headers were not provided so the content base cannot be loaded."), e);
        }
        return contextHeaders;
    }

    // Main recursion routine to build the content graph
    public void buildGraph(ContentLink graphLink) throws TouchContextException {
        ContentNode graphNode = (ContentNode) graphLink.getTarget();
        Node contextField = this.contextGraph.getNode(graphNode.getContextId());

        if (graphNode.getId().equalsIgnoreCase(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId())) {
            // Context headers are not persisted
            return;
        }
        graphLink.setContextLink(this.contextGraph.getLink(graphLink.getContextLinkId()));
        this.addLink(graphLink);
        if (contextField.getNodeType() == NodeType.CONTEXT) {
            // This node is actually a reference to another base
            ContentGraph refGraph = (ContentGraph) this.getRefGraphById((String) graphNode.getValue());
            //ContentGraph refGraph = (ContentGraph)this.getRefGraphById(graphLink.getLinkId());
            if (refGraph == null) {
                throw new TouchContextException("The content graph for context reference field " + contextField.getId() +
                        " could not be found. Context reference fields must have corresponding content");
            }
            // Set the value for the ref field to be the content-id (Content Graph ID)
            //graphNode.setValue(graphLink.getLinkId());
            graphNode.setValue(refGraph.getId());
        } else if (contextField.getNodeType() == NodeType.OBJECT) {
            LinkedHashMap<String, Object> nodeValues = (LinkedHashMap<String, Object>) graphNode.getValue();
            // Map node is an object, not a value
            graphNode.setValue(NodeType.OBJECT.toString());
            for (Map.Entry<String, Object> nodeValue : nodeValues.entrySet()) {
                ContentNode node = new ContentNode((Field) this.contextGraph.getNode(nodeValue.getKey()), nodeValue.getValue());
                ContentLink link = new ContentLink(this.getId(), graphNode, node, 0);
                this.addLink(link);
                this.buildGraph(link);
            }
        } else if (contextField.getNodeType() == NodeType.CONTEXT_ARRAY) { // This is an list of objects in a separate context (referenced graphs)
            List<Object> arrayForms = (ArrayList<Object>) graphNode.getValue();
            // The value is just placeholder indicator of the array type - i.e. [CONTEXT_ARRAY]
            graphNode.setValue(contextField.getNodeType().toString());
            // All members in the array will have the same root
            Field arrayContextField = graphNode.getContextField();
            // Context array fields will only ever have one target context type - so we can derive the context using the array target link
            Field memberContextField = (Field) this.getContext().getLinkBySourceId(arrayContextField.getId()).getTarget();
            int arrayCounter = 1; // Use 1-based array for naming array lists
            for (Object arrayFormObject : arrayForms) {
                ContentGraph refGraph = ContentGraph.fromMap(this.contextGraphProvider, this.scope, this.getLocale(), (Map<String, Object>) arrayFormObject);
                // If there is no ID set, or if the ID is the same as the context ID then we consider this to be new content
                boolean isNewContent = refGraph.getId() == null || refGraph.getId().isEmpty() || refGraph.getId().equals(refGraph.getContext().getId());
                // If this is new content then we use the array index as a placeholder ID - to be replaced later. Otherwise construct a new content ID based on the touchpoint ID and the array index
                String identifier = isNewContent ? Integer.toString(arrayCounter) : refGraph.getId().toLowerCase() + Integer.toString(arrayCounter);
                ContentNode memberContentNode = new ContentNode(memberContextField, arrayFormObject);
                // Append the unique identifier to the content node so that we can map the field later in the ContentGraphService save op
                memberContentNode.setId(memberContextField.getId() + "#" + identifier);
                ContentLink link = new ContentLink(this.getId(), graphNode, memberContentNode, arrayCounter);
                // USe natural sort order in case the array has been shuffled/re-ordered as part of an edit
                link.setSortIndex(arrayCounter - 1);

                // Reset the node value to be the ID of the new ref base - this is the reference value
                if (isNewContent) refGraph.setId(memberContentNode.getId());
                memberContentNode.setValue(refGraph.getId());
                // Collect the ref graph into this content graph
                this.getRefGraphs().put(refGraph.getId(), refGraph);
                this.addLink(link);
                this.buildGraph(link);
                arrayCounter++;
            }
        } else if (contextField instanceof ListableField && graphNode.getValue() instanceof List) { // This is a list node
            List<Object> nodeValues = (ArrayList<Object>) graphNode.getValue();
            graphNode.setValue(graphNode.getNodeType().toString());
            int arrayCounter = 0;
            for (Object nodeValue : nodeValues) {
                DataResource dataResource = new DataResource((String) nodeValue);
                ContentNode node = new ContentNode(dataResource, nodeValue);
                ContentLink link = new ContentLink(this.getId(), graphNode, node, arrayCounter);
                this.addLink(link);
                arrayCounter++;
            }
        }
    }

    private void bindContext() throws TouchContextException {
        for (Link link : this.getLinks().values()) {
            ContentLink contentLink = (ContentLink) link;
            for (Link contextLink : this.contextGraph.getLinks().values()) {
                if (((ContentLink) link).getContextLinkId().equalsIgnoreCase(contextLink.getId())) {
                    contentLink.setContextLink(contextLink);
                    this.addLink(contentLink);
                }
            }
        }
    }

    public Link findFirstProperty(String resourceId) {
        for (Link link : this.getLinks().values()) {
            ContentNode propertyNode = (ContentNode) link.getTarget();
            ContextResourceId targetResource = new ContextResourceId(propertyNode.getContextId());
            if (targetResource.resourceMatches(resourceId)) {
                return link;
            }
        }
        return null;
    }

    public Link findFirstPropertyByValue(String value) {
        for (Link link : this.getLinks().values()) {
            ContentNode propertyNode = (ContentNode) link.getTarget();
            String targetValue = (String) propertyNode.getValue();
            if (targetValue.equalsIgnoreCase(value)) {
                return link;
            }
        }
        return null;
    }

    public Object findFirstPropertyValue(String resourceId) {
        Link link = this.findFirstProperty(resourceId);
        if (link != null) {
            ContentNode node = (ContentNode) link.getTarget();
            return node.getValue();
        }
        return null;
    }

    public String findFirstPropertyValueAsString(String resourceId) {
        Object property = this.findFirstPropertyValue(resourceId);
        if (property != null) {
            return (String) property;
        }
        return "";
    }

    public String toJsonData() {
        LinkedHashMap<String, Object> jsonDataMap = toJsonDataMap();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(jsonDataMap);
        } catch (JsonProcessingException e) {
            throw new TouchContextException("JSON Processing error", e);
        }
    }

    public LinkedHashMap<String, Object> toJsonDataMap() {
        LinkedHashMap<String, Object> jsonDataMap = new LinkedHashMap<>();
        Collection<Link> rootLinks = this.getRootLinks().values();
        for (Link rootLink : rootLinks) {
            buildJsonDataMap(jsonDataMap, (ContentNode) rootLink.getTarget());
        }
        addContextHeaders(jsonDataMap);
        return jsonDataMap;
    }

    protected void buildJsonDataMap(Map<String, Object> map, ContentNode node) throws TouchContextException {
        Field contextField = node.getContextField();
        // Recurse children
        Collection<Link> targetLinks = super.getLinks(node).values();
        Map<String, Object> dataObjectMap = new LinkedHashMap<>();
        if (contextField instanceof ContextReference) {
            String contentRefId = (String) node.getValue();
            ContentGraph refGraph = (ContentGraph) this.getRefGraphById(contentRefId);
            if (refGraph == null) {
                throw new TouchContextException(String.join("", "Could not load referenced content: ", node.getId(), ", ", contentRefId));
            }
            try {
                Map<String, Object> refMap = refGraph.toJsonDataMap();
                map.put(contextField.getId(), refMap);
                return;
            } catch (Exception e) {
                throw new TouchContextException("Could not inject referenced base", e);
            }
        }
        int propCount = targetLinks.size();
        if (propCount > 0) {
            if (contextField.getNodeType().equals(NodeType.CONTEXT_ARRAY)) {
                ContextArray contextArrayField = (ContextArray) contextField;
                List<Object> arrayObjects = new LinkedList<>();
                map.put(contextArrayField.getId(), arrayObjects);
                // Ensure correct sortIndex is applied to the data
                LinkedList orderedLinks = targetLinks.stream().sorted(Comparator.comparing(Link::getSortIndex)).collect(Collectors.toCollection(LinkedList::new));
                for (Object link : orderedLinks) {
                    // There should only be one type of content in the array (defined by the referenced context)
                    Map<String, Object> arrayMap = new LinkedHashMap<>();
                    buildJsonDataMap(arrayMap, (ContentNode)((ContentLink)link).getTarget());
                    arrayObjects.add(arrayMap.entrySet().iterator().next().getValue());
                }
                return;
            } else if (contextField.getNodeType().equals(NodeType.OBJECT)) {
                map.put(node.getId(), dataObjectMap);
                for (Link targetLink : targetLinks) {
                    ContentNode targetNode = (ContentNode) targetLink.getTarget();
                    // Recurse
                    buildJsonDataMap(dataObjectMap, targetNode);
                }
            } else if (contextField instanceof ListableField && ((ListableField) contextField).isMultiValueDataList()) {
                // This is a multi-value list field - add a container for the value strings
                List<String> valuesList = new LinkedList<>();
                map.put(node.getId(), valuesList);
                // Add all the values as strings
                for (Link targetLink : targetLinks) {
                    ContentNode targetNode = (ContentNode) targetLink.getTarget();
                    valuesList.add((String) targetNode.getValue());
                }
            }
        } else {
            // This is a value (Leaf) element - add value node
            map.put(node.getId(), node.getValue());
        }
    }

    private void addContextHeaders(Map<String, Object> map) {
        // Add context headers for correlation
        Map<String, Object> contextHeaders = new LinkedHashMap<>();
        map.put(ContextHeaders.FieldEnum.CONTEXT_HEADERS.getContextId().getId(), contextHeaders);
        contextHeaders.put(ContextHeaders.FieldEnum.CONTEXT_ID.getContextId().getId(), this.getContext().getId());
        contextHeaders.put(ContextHeaders.FieldEnum.CONTENT_ID.getContextId().getId(), this.getContext().getContentKeyField().getId());
        // Compile header title text
        final StringBuilder builder = new StringBuilder();
        Map<Integer, String> headersMap = new LinkedHashMap<>();
        for (Link link : this.getContext().getLinks().values()) {
                Field headerField = (Field) link.getTarget();
                if (headerField.isHeader()) {
                    headersMap.put(link.getSortIndex(), headerField.getId());
                }
        }
        LinkedHashMap<Integer, String> headersMapSorted = headersMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (String headerId : headersMapSorted.values()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals(headerId)) {
                    builder.append(entry.getValue() + "  ");
                }
            }
        }
        if (builder.length() == 0) {
            builder.append(this.getTitle());
        }
        contextHeaders.put(ContextHeaders.FieldEnum.HEADER_TEXT.getContextId().getId(), builder.toString().trim());
    }

    public List<ContentLink> getContentLinks(String contextFieldId) {
        List<ContentLink> links = new LinkedList<>();
        for (Link link : this.getLinks().values()) {
            ContentNode sourceNode = (ContentNode) ((ContentLink) link).getSource();
            if (sourceNode.getContextId().equals(contextFieldId)) {
                links.add((ContentLink) link);
            }
        }
        return links;
    }

    public ContextGraph getContext() {
        this.mapContext();
        return this.contextGraph;
    }

    @Override
    public ContextResourceId getContextResourceId() {
        if (this.getContext() != null) {
            return this.contextGraph.getContextResourceId();
        }
        return null;
    }

    public void setContextGraph(ContextGraph contextGraph) {
        this.contextGraph = contextGraph;
    }

    public UUID getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(UUID lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getContextDomainKeyFieldId() {
        if (contextDomainKeyFieldId == null || contextDomainKeyFieldId.isEmpty()) {
            contextDomainKeyFieldId = contextGraph.getContentDomainKeyFieldId();
        }
        return contextDomainKeyFieldId;
    }

    public void setContextDomainKeyFieldId(String contextDomainKeyFieldId) {
        this.contextDomainKeyFieldId = contextDomainKeyFieldId;
    }

    public String getContextDomainKey() {
        if (contextDomainKey == null || contextDomainKey.isEmpty()) {
            Optional<Link> keyLink = this.getLinks().values().stream().filter(l -> (l.getTarget().getContextResourceId().resourceMatches(getContextDomainKeyFieldId()))).findFirst();
            contextDomainKey = keyLink.isPresent() ? ((ContentLink) keyLink.get()).getValueAsString() : null;
        }
        return contextDomainKey;
    }

    @Override
    // Look for the root context node of this graph and retrieve the sharing type. Sharing type indicates whether a node for this graph can be shared by other graphs
    public GraphSharingType getGraphSharingType() {
        return this.getContext().getGraphSharingType();
    }

    public void setContextDomainKey(String contextDomainKey) {
        this.contextDomainKey = contextDomainKey;
    }
}