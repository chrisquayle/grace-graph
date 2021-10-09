package com.chrisq.grace.graph;

public enum LinkType {
    PROPERTY("Basic Property"),

    /***
     * The property is a reference to a separate context (object graph)
     *
     * SOURCE NODE ---------- REF NODE ----id----- [REFERENCED GRAPH]
     *
     */
    SHARED_CONTEXT_REF("Shared Context Reference"),

    /***
     * The property data is embedded in the 'parent' content graph
     *
     * SOURCE NODE ---------- REF NODE --------- REF FIELD 1 VALUE
     *                     |
     *                     |______________REF FIELD 2 VALUE
     *
     */
    BOUND_CONTEXT_REF("Bound context referecne");

    private String label;

    LinkType(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
