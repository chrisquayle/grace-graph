package com.chrisq.grace.graph;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.chrisq.grace.graph.content.ContentNode;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.context.data.DataResource;
import com.chrisq.grace.graph.context.form.field.*;

import java.util.Map;

/**
 * TODO: ContentUpdate file header
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataResource.class, name = "dataResource"),
        @JsonSubTypes.Type(value = Field.class, name = "contextNode"),
        @JsonSubTypes.Type(value = AbstractField.class, name = "contextField"),
        @JsonSubTypes.Type(value = DateField.class, name = "dateField"),
        @JsonSubTypes.Type(value = DateTimeField.class, name = "datetimeField"),
        @JsonSubTypes.Type(value = EmailField.class, name = "emailField"),
        @JsonSubTypes.Type(value = ContextArray.class, name = "contextArray"),
        @JsonSubTypes.Type(value = NumberField.class, name = "numberField"),
        @JsonSubTypes.Type(value = TextField.class, name = "textField"),
        @JsonSubTypes.Type(value = TimeField.class, name = "timeField"),
        @JsonSubTypes.Type(value = BooleanField.class, name = "booleanField"),
        @JsonSubTypes.Type(value = ContentNode.class, name = "contentNode"),
        @JsonSubTypes.Type(value = ContextReference.class, name = "contextReference")})
public interface Node extends Resource, Temporal {
    NodeType getNodeType();
    void setNodeType(NodeType nodeType);
    Boolean isArray();
    Map<String, Object> toSchemaMap() throws TouchContextException;
    String toGraphJson();
}
