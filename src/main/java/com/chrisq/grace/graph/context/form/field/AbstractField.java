package com.chrisq.grace.graph.context.form.field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrisq.grace.graph.GraphNode;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.resource.ResourceException;
import com.chrisq.grace.graph.resource.ResourceType;
import com.chrisq.grace.graph.resource.TemporalResource;
import com.chrisq.grace.graph.util.Mapper;

import java.util.*;

/**
 * TODO: ContentUpdate file header
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractField extends GraphNode implements Field {
    private UUID versionId = UUID.randomUUID();
    @JsonProperty(value = "default")
    private Object defaultValue = null;
    private boolean hidden = false;
    private boolean readOnly = false;
    private boolean required = false;
    private boolean searchable = false;
    private boolean header = false;
    private FieldClassification classification = FieldClassification.INTERNAL;

    private ObjectMapper objectMapper = Mapper.getObjectMapper();

    protected AbstractField(){super();}
    public AbstractField(TemporalResource fromResource){
        super(fromResource);
    }
    public AbstractField(ContextResourceId resourceId, NodeType fieldType) throws ResourceException {
        super(resourceId.getId(), fieldType);
        if(!this.getResourceType().equals(ResourceType.CONTEXT_FIELD) && !this.getResourceType().equals(ResourceType.CONTEXT)){
            throw new ResourceException("Could not construct a valid context field from the provided resource ID: " +
                    resourceId + ". The type part must be tcxf for a context field, or tcx for a context reference. " +
                    "URN Format: {scope}:{type}:{component}:[{id}]");
        }
    }

    /**
     * Return schema property
     */
    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        putIfNotNull(node, "default", this.defaultValue);
        putIfTrue(node,"readonly", this.readOnly);
        putIfTrue(node, "required", this.required);
        return node;
    }

    /**
     * Return a container for json fragment that supports Alpaca forms options for the field
     */
    @Override
    public Map<String, Object> toOptionMap(){
        Map<String, Object> field = new LinkedHashMap<>();
        putIfTrue(field, "hidden", this.hidden);
        return field;
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

        Map<String, Object> dependencies = new LinkedHashMap<>();
        dependencies.put("advanced", "type");

        Map<String, Object> properties = new LinkedHashMap<>();
        schemaRoot.put("properties", properties);
        LinkedHashMap<String, Object> idMap = putNewMetaPropertyMap(properties, "id", NodeType.TEXT_STRING, this.getId(), true, true);

        LinkedHashMap<String, Object> typeMap = putNewMetaPropertyMap(properties, "type", NodeType.TEXT_STRING, NodeType.TEXT_STRING.getLabel(), true, true);
        LinkedList typeSelect = new LinkedList();
        typeMap.put("enum", typeSelect);
        for (NodeType type : NodeType.values()) {
            typeSelect.add(type.name());
        }

        LinkedHashMap<String, Object> classificationMap = putNewMetaPropertyMap(properties, "securityClassification", NodeType.TEXT_STRING, NodeType.TEXT_STRING.getLabel(), true, false);
        LinkedList classificationSelect = new LinkedList();
        classificationMap.put("enum", classificationSelect);
        for (FieldClassification classification : FieldClassification.values()) {
            classificationSelect.add(classification.name());
        }

        putNewMetaPropertyMap(properties, "title", NodeType.TEXT_STRING, this.getTitle(), true);
        putNewMetaPropertyMap(properties, "description", NodeType.TEXT_STRING, this.getDescription(), false);
        putNewMetaPropertyMap(properties, "hidden", NodeType.BOOLEAN, this.hidden, false);
        putNewMetaPropertyMap(properties, "required", NodeType.BOOLEAN, this.required, false);
        putNewMetaPropertyMap(properties, "readonly", NodeType.BOOLEAN, this.readOnly, false);
        putNewMetaPropertyMap(properties, "default", NodeType.TEXT_STRING, this.defaultValue, false, true);
        return schemaRoot;
    }

    @Override
    public Map<String, Object> toMetaOptionMap() {
        Map<String, Object> optionRoot = new LinkedHashMap<>();
        Map<String, Object> fields = putNewMap(optionRoot, "fields");

        Map<String, Object> type = putNewMap(fields, "type");
        List<String> typeLabels = putNewList(type, "optionLabels");
        for (NodeType typeEntry : NodeType.values()) {
            typeLabels.add(typeEntry.getLabel());
        }

        Map<String, Object> classification = putNewMap(fields, "securityClassification");
        List<String> classificationLabels = putNewList(classification, "optionLabels");
        for (FieldClassification classificationEntry : FieldClassification.values()) {
            classificationLabels.add(classificationEntry.getLabel());
        }

        LinkedHashMap<String, Object> defaultOptions = putNewMap(fields, "default");
        putResourceBrowseButton(defaultOptions);
        return optionRoot;
    }

    // Add a button which will popup a resource creator/selector for the field
    protected void putResourceBrowseButton(Map<String, Object> map){
        Map<String, Object> buttons = putNewMap(map, "buttons");
        Map<String, Object> button = putNewMap(buttons, "browse");
        button.put("title", "...");
        button.put("click", "<script>function(){handleResourceBrowseClick(this);}</script>");
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> dataRoot = new LinkedHashMap<>();
        dataRoot.put("id", this.getId());
        dataRoot.put("title", this.getTitle());
        dataRoot.put("description", this.getDescription());
        dataRoot.put("default", this.getDefaultValue());
        dataRoot.put("hidden", this.isHidden());
        dataRoot.put("required", this.isRequired());
        dataRoot.put("readonly", this.isReadOnly());
        dataRoot.put("type", this.getNodeType().name());
        dataRoot.put("securityClassification", this.classification.name());
        return dataRoot;
    }

    // Serialisation helpers
    @Override
    public String toSchemaJson() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(super.toSchemaMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate Schema JSON", e);
        }
    }

    @Override
    public String toOptionJs() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toOptionMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate options JSON", e);
        }
    }

    @Override
    public String toMetaSchemaJson() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaSchemaMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta schema JSON", e);
        }
    }

    @Override
    public String toMetaOptionJs() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaOptionMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta option JSON", e);
        }
    }

    @Override
    public String toMetaDataJson() throws TouchContextException {
        try {
            return scrubJs(objectMapper.writeValueAsString(this.toMetaDataMap()));
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not generate meta data JSON", e);
        }
    }

    // Map utility methods for Form and Field JS generation

    // Remove placeholder <script> tags to produce valid JS output
    public static String scrubJs(String json){
        json = json.replaceAll(".<script>", "");
        json = json.replaceAll("</script>.", "");
        return json;
    }

    // Override for readonly
    public static LinkedHashMap<String, Object> putNewMetaPropertyMap(Map<String, Object> putIntoMap, String id, NodeType type, Object defaultValue, boolean required, boolean readOnly){
        LinkedHashMap<String, Object> propertyMap = putNewMetaPropertyMap(putIntoMap, id, type, defaultValue, required);
        propertyMap.put("readonly", readOnly);
        return propertyMap;
    }

    public static LinkedHashMap<String, Object> putNewMetaPropertyMap(Map<String, Object> putIntoMap, String id, NodeType type, Object defaultValue, boolean required){
        LinkedHashMap<String, Object> propertyMap = new LinkedHashMap<>();
        putIntoMap.put(id, propertyMap);
        propertyMap.put("id", id);
        propertyMap.put("title", id);
        propertyMap.put("description", id);
        putIfNotNull(propertyMap, "default", defaultValue);
        propertyMap.put("type", type.getSchemaType());
        putIfNotNull(propertyMap, "format", type.getSchemaFormat());
        propertyMap.put("required", required);

        return propertyMap;
    }


    public ContextResourceId getContextResourceId(){
        return new ContextResourceId(this.getId());
    }

    public UUID getVersionId() {
        return versionId;
    }

    public void setVersionId(UUID versionId) {
        this.versionId = versionId;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public FieldClassification getClassification() {
        return classification;
    }

    public void setClassification(FieldClassification classification) {
        this.classification = classification;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    @Override
    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "nodeType='" + this.getNodeType() + '\'' +
                ", id='" + this.getId() + '\'' +
                ", title='" + this.getTitle() + '\'' +
                ", state='" + this.getState().name() + '\'' +
                ", defaultValue=" + defaultValue +
                ", hidden=" + hidden +
                ", readOnly=" + readOnly +
                ", required=" + required +
                ", searchable=" + searchable +
                ", header=" + header +
                ", classification=" + classification +
                '}';
    }
}
