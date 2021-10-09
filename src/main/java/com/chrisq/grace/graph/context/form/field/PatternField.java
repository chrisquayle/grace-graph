package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.resource.ResourceException;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PatternField extends AbstractField {
    private String pattern;
    private Integer minLength = null;
    private Integer maxLength = null;

    protected PatternField(){
        super();
    }
    public PatternField(ContextResourceId resourceId, NodeType fieldType) throws ResourceException {
        super(resourceId, fieldType);
    }

    /**
     * Return json schema property
     */
    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        putIfNotNull(node, "pattern", this.pattern);
        putIfNotNull(node, "minLength", this.minLength);
        putIfNotNull(node, "maxLength", this.maxLength);
        return node;
    }

    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> map = super.toMetaSchemaMap();
        Map<String, Object> properties = (LinkedHashMap)map.get("properties");
        // Add listable meta properties
        putNewMetaPropertyMap(properties, "pattern", NodeType.TEXT_STRING, null, false);
        putNewMetaPropertyMap(properties, "minLength", NodeType.NUMBER, null, false);
        putNewMetaPropertyMap(properties, "maxLength", NodeType.NUMBER, null, false);
        return map;
    }

    @Override
    public Map<String, Object> toMetaOptionMap() {
        Map<String, Object> map = super.toMetaOptionMap();
        Map<String, Object> fieldsMap = (Map<String, Object>)map.get("fields");
        LinkedHashMap<String, Object> fieldMap = putNewMap(fieldsMap, "pattern");
        fieldMap.put("helper", "TODO: Canned regex selector/builder");
        return map;
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> map = super.toMetaDataMap();
        putIfNotNull(map, "pattern", this.pattern);
        putIfNotNull(map, "minLength", this.minLength);
        putIfNotNull(map, "maxLength", this.maxLength);
        return map;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String toString() {
        return "TextField{" +
                super.toString() +
                ", pattern='" + pattern + '\'' +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                '}';
    }
}
