package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

public class TimeField extends DateField {

    protected TimeField(){super();}
    public TimeField(ContextResourceId resourceId) {
        super(resourceId);
        this.setNodeType(NodeType.TIME_STRING);
    }
}
