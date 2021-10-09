package com.chrisq.grace.graph.context.data;

/**
 * Created by cquayle on 6/19/2017.
 */
// TODO: Review this
public class ListItem {
    private String id;
    private String text;
    private String description;

    public ListItem(String id) {
        this.id = id;
    }
    public ListItem(String id, String text, String description){
        this(id);
        this.text = text;
        this.description = description;
    };


    public String getValue() {
        return this.id;
    }

    public void setValue(String value) {
        this.id = value;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
