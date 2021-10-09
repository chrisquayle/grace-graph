package com.chrisq.grace.graph.content.persistence;

import com.chrisq.grace.graph.content.ContentGraph;
import com.chrisq.grace.graph.content.ContentUpdate;
import com.chrisq.grace.graph.context.TouchContextException;

import java.util.List;
import java.util.UUID;

public interface ContentData {
    // CONTENT GRAPH
    String saveContentGraph(ContentGraph graph) throws TouchContextException;

    UUID saveUpdate(ContentUpdate update);

    ContentGraph getContentGraph(String contentGraphId) throws TouchContextException;

    ContentUpdate getUpdate(UUID updateId);

    List<ContentUpdate> getUpdates(String touchpointId);

    List<String> getGraphIds(String linkId, String value);

}
