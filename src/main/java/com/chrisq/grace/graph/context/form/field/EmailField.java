package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

public class EmailField extends AbstractField {

    protected EmailField(){super();}
    public EmailField(ContextResourceId resourceId) {
        super(resourceId, NodeType.EMAIL_STRING);
    }
}
