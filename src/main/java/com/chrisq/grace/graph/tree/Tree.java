package com.chrisq.grace.graph.tree;

import com.chrisq.grace.graph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 */
public class Tree extends Branch {
    private static final Logger LOG = LoggerFactory.getLogger(Tree.class);
    protected final Graph graph;

    public Tree(Graph graph) {
        this.graph = graph;
        this.initialize();
    }

    public void initialize() {
        this.setResourceId(graph.getId());
        this.setTitle(graph.getTitle());
        super.setNode(new GraphNode(graph.getId(), NodeType.CONTEXT_ARRAY));
        this.setPath("/");
        Map<String, Link> rootProperties = graph.getRootLinks();
        for (Link rootLink : rootProperties.values()) {
            loadBranch(this, rootLink);
        }
    }

    public void setSelected(String resourceId){
        Branch match = find(resourceId);
        if(match != null){
            match.setSelected(true);
        }
    }

    public Branch getSelected(){
        for(Branch branch : this.getBranches()){
            Branch selected = getSelected(branch);
            if(selected != null){
                return selected;
            }
        }
        return null;
    }

    public Branch getSelected(Branch branch){
        if(branch.isSelected()) return branch;
        Branch selected;
        for(Branch child : branch.getBranches()){
            selected = getSelected(child);
            if(selected != null){
                return selected;
            }
        }
        return null;
    }

    public Branch find(String resourceId){
        Branch result;
        for(Branch branch : this.getBranches()){
            result = find(branch, resourceId);
            if(result != null){
                return result;
            }
        }
        return null;
    }

    public Branch find(Branch branch, String resourceId){
        if(branch.getNode().getId().equalsIgnoreCase(resourceId)){
            return branch;
        }
        Branch result;
        for(Branch child : branch.getBranches()){
            result = find(child, resourceId);
            if(result != null){
                return result;
            }
        }
        return null;
    }

    protected void loadBranch(Branch parent, Link link) {
        GraphNode thisNode = (GraphNode)link.getSource();
        Branch thisBranch = new Branch((Node) link.getTarget());
        thisBranch.setLinkId(link.getId());
        boolean isRoot = parent.getPath().equals("/");
        String path = "/";
        if(isRoot){
            path = thisBranch.getId();
        }
        if(!isRoot){
            path = parent.getPath() + "/" + thisBranch.getId();
        }
        if(thisNode.isArray()) {
            // We need to move back a level in the path - to reshape the way that arrays are described
            // Normal: array/item/value Required for alpaca path refs: array[0]/value
            if(!parent.getPath().endsWith("[0]")){
                path = parent.getPath() + "[0]";
            }
            else {
                // This must be an array within an array
                path = parent.getPath() + "/" + thisBranch.getId() + "[0]";
            }
        }
        thisBranch.setPath(path);
        parent.addBranch(thisBranch);
        LOG.debug("Load branch: " + parent.toString() + " -> " + link.toString());
        // Recurse children
        Map<String, Link> targetProperties = graph.getLinks(thisBranch.getNode());
        for (Link targetProperty : targetProperties.values()) {
            loadBranch(thisBranch, targetProperty);
        }
    }
}

