package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

import java.util.LinkedHashMap;
import java.util.Map;

public class TextField extends ListableField{
    private String placeholder;

    protected TextField(){super();}
    public TextField(ContextResourceId resourceId) {
        super(resourceId, NodeType.TEXT_STRING);
    }

    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        //node.put("default", this.getDefaultValue() == null ? "" : this.getDefaultValue());
        return node;
    }

    @Override
    public Map<String, Object> toOptionMap() {
        Map<String, Object> options = super.toOptionMap();
        putIfNotNull(options, "placeholder", this.placeholder);
        if(!this.isRequired()) {
            options.put("allowOptionalEmpty", true);
        } else {
            //options.put("hideInitValidationError", true);
        }
        return options;
    }

    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> map = super.toMetaSchemaMap();
        Map<String, Object> properties = (LinkedHashMap)map.get("properties");
        putNewMetaPropertyMap(properties, "placeholder", NodeType.TEXT_STRING, null, false);
        return map;
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> map = super.toMetaDataMap();
        putIfNotNull(map, "placeholder", this.placeholder);
        return map;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
