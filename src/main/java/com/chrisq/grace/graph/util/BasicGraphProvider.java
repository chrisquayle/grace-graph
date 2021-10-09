package com.chrisq.grace.graph.util;

import com.chrisq.grace.graph.Graph;
import com.chrisq.grace.graph.GraphProvider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BasicGraphProvider implements GraphProvider {

    private List<Graph> directory = new ArrayList<>();

    public BasicGraphProvider(Graph graph){
        this.directory.add(graph);
    }

    public List<Graph> getDirectory() {
        return directory;
    }

    @Override
    public Graph getGraph(String graphId, Instant asAtUtc) {
        for(Graph graph: directory){
            if(graph.getId().equalsIgnoreCase(graphId)){
                return graph;
            }
        }
        return null;
//        Map<Instant, Graph> versions = new TreeMap<>(Collections.reverseOrder());
//        for(Graph form: directory){
//            if(form.getId().equalsIgnoreCase(graphId)){
//                versions.put(form.getCreatedUtc(), form);
//            }
//        }
//        // Traverse all versions of the form and return the first that is applicable
//        for(Map.Entry<Instant, Graph> version : versions.entrySet()){
//            if(versions.size() == 1 || version.getKey().isBefore(asAtUtc)){
//                return version.getValue();
//            }
//        }
//        return null;
    }
}
