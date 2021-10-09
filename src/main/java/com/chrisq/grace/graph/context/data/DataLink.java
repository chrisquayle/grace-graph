package com.chrisq.grace.graph.context.data;

import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.Node;

public class DataLink extends Link {
    protected DataLink(){super();}
    public DataLink(Node source, Node target, int sortIndex) {
        this(source, target);
        this.setSortIndex(sortIndex);
    }
    public DataLink(Node source, Node target) {
        super(source, target);
    }
}
