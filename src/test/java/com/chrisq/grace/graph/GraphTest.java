package com.chrisq.grace.graph;

import com.chrisq.grace.graph.content.ContentGraph;
import com.chrisq.grace.graph.context.ContextGraph;
import com.chrisq.grace.graph.context.form.ContextForm;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.TestCase.assertTrue;

/**
 * TODO: ContentUpdate file header
 */
public class GraphTest {
    private GraphSample sample = new GraphSample();
    private ContentGraph contentGraph = sample.getSampleContentGraph();
    private ContextGraph contextGraph = sample.getSampleContextGraph();

    @Test
    public void rootPropertiesTest() {
        Collection<Link> roots = this.contextGraph.getRootLinks().values();
        for (Link p : roots) System.out.println("Root: " + p.toString());
        assertTrue(roots.size() > 0);
    }

    @Test
    public void contentGraphD3Test() {
        System.out.println("D3 CONTENT GRAPH\n");
        System.out.println(contentGraph.toD3GraphJson());
    }

    @Test
    public void contextGraphJsonTest(){
        ContextForm form = new ContextForm(contentGraph.getContext());
        System.out.println("STARSYSTEM Context ToString():  " + contentGraph.toString());
        System.out.println(form.getContext().toJsonString());
        for(Graph graph : contextGraph.getRefGraphs().values()){
            System.out.println("\n" + ((ContextGraph)graph).toJsonString());
        }
    }

    @Test
    public void contentGraphToStringTest() {
        System.out.println("SOLARSYSTEM ContentGraph ToString():  " + contentGraph.toString());
    }

    @Test
    public void contentGraphToJsonDataTest(){
        System.out.println("SOLARSYSTEM JSON DATA:  " + contentGraph.toJsonData());
    }
}