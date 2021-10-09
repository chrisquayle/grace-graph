package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

import java.util.LinkedHashMap;
import java.util.Map;

public class NumberField extends TextField {
    private Integer multipleOf = null;
    private Integer maximum = null;
    private Integer minimum = null;

    protected NumberField(){super();}
    public NumberField(ContextResourceId resourceId) {
        super(resourceId);
        this.setNodeType(NodeType.NUMBER);
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public Integer getMultipleOf() {
        return multipleOf;
    }

    public void setMultipleOf(Integer multipleOf) {
        this.multipleOf = multipleOf;
    }

    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        putIfNotNull(node, "maximum", this.maximum);
        putIfNotNull(node, "minimum", this.minimum);
        putIfNotNull(node, "multipleOf", this.multipleOf);
        return node;
    }

    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> map = super.toMetaSchemaMap();
        Map<String, Object> properties = (LinkedHashMap)map.get("properties");
        // Add listable meta properties
        putNewMetaPropertyMap(properties, "minimum", NodeType.NUMBER, null, false);
        putNewMetaPropertyMap(properties, "maximum", NodeType.NUMBER, null, false);
        putNewMetaPropertyMap(properties, "multipleOf", NodeType.NUMBER, null, false);
        return map;
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> map = super.toMetaDataMap();
        putIfNotNull(map, "minimum", this.minimum);
        putIfNotNull(map, "maximum", this.maximum);
        putIfNotNull(map, "multipleOf", this.multipleOf);
        return map;
    }

    @Override
    public String toString() {
        return "NumberField{" +
                "multipleOf=" + multipleOf +
                ", maximum=" + maximum +
                ", minimum=" + minimum +
                '}';
    }
}
