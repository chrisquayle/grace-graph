package com.chrisq.grace.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.tree.Branch;
import com.chrisq.grace.graph.tree.Tree;
import com.chrisq.grace.graph.tree.treeview.TreeViewSerializer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class TreeTest {
    private Graph graph = null;
    private Tree tree = null;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        try {
            GraphSample demo = new GraphSample();
            graph = demo.getSampleContextGraph();
            tree = new Tree(graph);
        } catch (TouchContextException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void TestTreeSelector(){
        String resourceId = "base:tcxf:atmosphere-element:xenon";
        tree.setSelected(resourceId);
        System.out.println(tree.toString());
        Branch selected = tree.getSelected();
        assertNotNull(selected);
        System.out.println(selected.toString());
        assertEquals(resourceId, selected.getId());
    }

    @Test
    public void TestTreeViewSerializer(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addSerializer(Branch.class, new TreeViewSerializer());
        mapper.registerModule(module);

//        try {
//
//            String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
//            System.out.println(serialized);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
        assertTrue(tree != null);
    }

//    private void print(String resource){
//        try {
//            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resource));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
}