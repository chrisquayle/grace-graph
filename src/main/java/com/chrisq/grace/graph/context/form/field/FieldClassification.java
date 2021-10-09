package com.chrisq.grace.graph.context.form.field;

public enum FieldClassification {
    PUBLIC("Public"),
    INTERNAL("Internal"),
    CONFIDENTIAL("Confidential"),
    RESTRICTED("Restricted"),
    PRIVATE("Private");

    private String label;

    FieldClassification(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }

}
