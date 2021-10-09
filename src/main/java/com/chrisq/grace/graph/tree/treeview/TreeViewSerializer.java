package com.chrisq.grace.graph.tree.treeview;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.chrisq.grace.graph.Node;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.form.field.Field;
import com.chrisq.grace.graph.tree.Branch;
import com.chrisq.grace.graph.tree.Tree;

import java.io.IOException;

public class TreeViewSerializer extends StdSerializer<Branch> {

    public TreeViewSerializer() {
        this(null);
    }

    public TreeViewSerializer(Class<Branch> t) {
        super(t);
    }

    @Override
    public void serialize(
            Branch value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        boolean isRoot = value instanceof Tree;
        if (isRoot) jgen.writeStartArray();
        jgen.writeStartObject();
        jgen.writeStringField("text", value.getTitle());
        jgen.writeStringField("resourceId", value.getId());
        jgen.writeStringField("linkId", value.getLinkId());
        jgen.writeStringField("path", value.getPath());

        jgen.writeObjectFieldStart("state");
        jgen.writeBooleanField("selected", value.isSelected());
        jgen.writeEndObject();

        Node resourceNode = value.getNode();
        if(resourceNode != null){
            jgen.writeObjectField("resource", value.getNode());
            if(resourceNode instanceof Field){
                serializeContext(jgen, (Field)resourceNode);
            }
        }
        if(!value.getBranches().isEmpty()) {
            jgen.writeObjectField("nodes", value.getBranches());
        }
        jgen.writeEndObject();
        if(isRoot) jgen.writeEndArray();
    }

    private void serializeContext(JsonGenerator jgen, Field resource) throws IOException {
        if(!resource.getContextResourceId().isBaseResource()){
            jgen.writeArrayFieldStart("tags");
            jgen.writeString("c");
            jgen.writeEndArray();
        }
        NodeType type = resource.getNodeType();
        String fieldIcon = null;
        if(resource.isArray()) {
            fieldIcon = "glyphicon glyphicon-repeat";
        }
        if(resource.getNodeType() == NodeType.CONTEXT_ARRAY){
            fieldIcon = "glyphicon glyphicon-link";
        }
        if(resource instanceof Field){
            if(((Field)resource).isHidden()){
                fieldIcon = "glyphicon glyphicon-eye-close";
            }
        }
        if(fieldIcon != null){
            jgen.writeStringField("icon", fieldIcon);
        }
    }
}

/*
https://github.com/jonmiles/bootstrap-treeview
    {
      text: "Node 1",
      icon: "glyphicon glyphicon-stop",
      selectedIcon: "glyphicon glyphicon-stop",
      color: "#000000",
      backColor: "#FFFFFF",
      href: "#node-1",
      selectable: true,
      state: {
        checked: true,
        disabled: true,
        expanded: true,
        selected: true
      },
      tags: ['available'],
      nodes: [
        {},
        ...
      ]
    }
*/
