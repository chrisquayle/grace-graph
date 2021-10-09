package com.chrisq.grace.graph.context.data;

import com.chrisq.grace.graph.AbstractGraph;
import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.Node;
import com.chrisq.grace.graph.resource.TemporalResource;

import java.util.LinkedList;
import java.util.Map;

public class DataGraph extends AbstractGraph {

    protected DataGraph() {
        super();
    }

    public DataGraph(String id) {
        super(id);
    }

    public DataGraph(TemporalResource fromResource) {
        super(fromResource);
    }

    public LinkedList<DataResource> getDataList(String id) {
        Node listResource = this.getNode(id);
        Map<String, Link> links = this.getLinks(listResource);
        LinkedList<DataResource> list = new LinkedList<>();
        for (Link link : links.values()) {
            DataResource resource = new DataResource((TemporalResource) link.getTarget());
            list.add(resource);
        }
        ;
        return list;
    }

}