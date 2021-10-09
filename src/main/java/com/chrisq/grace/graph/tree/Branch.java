package com.chrisq.grace.graph.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.chrisq.grace.graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Branch {
    private List<Branch> branches = new ArrayList<Branch>();
    private String resourceId;
    private String linkId;
    @JsonProperty(value="text")
    private String title;
    private String path;
    private Node node;
    private boolean selected = false;

    public Branch() {
    }

    public Branch(Node node) {
        this.node = node;
        this.resourceId = node.getId();
        this.title = node.getTitle() == null ? node.getId() : node.getTitle();
    }

    public void addBranch(Branch branch) {
        this.branches.add(branch);
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public Node getNode() {
        return node;
    }

    public String getId() {
        return this.resourceId;
    }

    public String getTitle() {
      return this.title;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
        this.title = this.title == null ? resourceId : this.title;
    }

    protected void setNode(Node node) {
        this.node = node;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkId() {
        return linkId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branches=" + branches +
                ", resourceId='" + resourceId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
