package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

import java.util.Map;

public class BooleanField extends AbstractField {

    protected BooleanField(){super();}
    public BooleanField(ContextResourceId resourceId) {
        super(resourceId, NodeType.BOOLEAN);
    }

    @Override
    public Map<String, Object> toOptionMap() {
        Map<String, Object> options = super.toOptionMap();
        options.put("type", "checkbox");
        return options;
    }
}
