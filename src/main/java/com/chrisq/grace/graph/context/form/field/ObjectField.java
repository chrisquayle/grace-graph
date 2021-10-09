package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

public class ObjectField extends AbstractField {

    protected ObjectField(){super();}
    public ObjectField(ContextResourceId resourceId) {
        super(resourceId, NodeType.OBJECT);
    }
}
