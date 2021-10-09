package com.chrisq.grace.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.chrisq.grace.graph.content.ContentGraph;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.tree.Tree;

import java.util.Map;

/**
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContextGraph.class),
        @JsonSubTypes.Type(value = ContentGraph.class)
})
public interface Graph extends Resource, Temporal {

    void addLink(Link  link) throws com.chrisq.grace.graph.resource.ResourceException;

    Map<String, Link> getLinks();

    Map<String, Link> getLinks(Node node);

    @JsonIgnore
    Map<String, Link> getRootLinks();

    Map<String, Graph> getRefGraphs();

    @JsonIgnore
    Tree toTree() throws TouchContextException;
}
