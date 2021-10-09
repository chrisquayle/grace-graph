package com.chrisq.grace.graph.content;

import com.chrisq.grace.graph.GraphProvider;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.util.BasicGraphProvider;
import com.chrisq.grace.graph.GraphSample;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentGraphTest {

    private ContextGraph contextGraph;
    private ContentGraph contentGraph;
    private ContextResourceId contextResourceId;
    private BasicGraphProvider basicGraphProvider;
    private GraphProvider contextGraphProvider;

    @Before
    public void setup() {
        contextGraphProvider = new BasicGraphProvider(GraphSample.getSampleContextGraph());
        contentGraph = GraphSample.getSampleContentGraph();
    }

    @Test
    public void shouldFindTheCorrectIdFromMap() {
        assertEquals("base:tcn:solar-system", contentGraph.getId());
    }
}