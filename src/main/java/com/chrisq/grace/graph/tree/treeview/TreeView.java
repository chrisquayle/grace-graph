package com.chrisq.grace.graph.tree.treeview;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.chrisq.grace.graph.Graph;
import com.chrisq.grace.graph.tree.Tree;

@JsonSerialize(using = TreeViewSerializer.class)
public class TreeView extends Tree {
    public TreeView(Graph graph) {
        super(graph);
    }
}
