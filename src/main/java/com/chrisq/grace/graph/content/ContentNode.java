package com.chrisq.grace.graph.content;

import com.chrisq.grace.graph.GraphNode;
import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.form.field.Field;

/**
 *
 */
public class ContentNode extends GraphNode {
    // The context link in which this node is the target (property)
    private Link contextLink = null;
    private Field contextField = null;
    private Object value;

    protected ContentNode(){super();}
    public ContentNode(Field context, Object value){
        super(context.getId(), NodeType.OBJECT);
        this.contextField = context;
        this.value = value;
    }

    public Object getValue() { return value; }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        if (super.getId() != null) {
            String value = this.getValue() == null ? "OBJECT" : this.getValue().toString();
            return String.join("", "{\"", super.getId(), "\": \"", value, "\"}");
        }
        return "";
    }

    public String getContextId(){
        // Remove any instance id sections from node ID keys to derive the context ID
        // E.g. t:references#abc1000/t:reference-source
        // E.g. t:references/t:references#abc1000
        String contextId = this.getId();
        int indexOfArray = this.getId().indexOf("#");
        if(indexOfArray > 0){
             contextId = this.getId().substring(0, indexOfArray);
        }
        return contextId;
    }

    public Field getContextField() {
        return contextField;
    }

    public Link getContextLink() {
        return contextLink;
    }

    public void setContextLink(Link contextLink) {
        this.contextLink = contextLink;
        this.contextField = (Field) contextLink.getTarget();
    }


}
