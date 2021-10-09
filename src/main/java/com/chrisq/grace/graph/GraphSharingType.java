package com.chrisq.grace.graph;

/***
 * Defines the type of content for context reference nodes.
 *
 */
public enum GraphSharingType {
    /***
     * The data is a reference to a separate graph
     *
     * GRAPH ---------- REF NODE ----id----- [REFERENCED GRAPH]
     *
     */
    SHARED_GRAPH("Shared graph"),

    /***
     * The data is embedded in the 'parent' content graph
     *
     * GRAPH ---------- REF NODE --------- REF FIELD 1 VALUE
     *                     |
     *                     |______________REF FIELD 2 VALUE
     *
     */
    BOUND_GRAPH("Bound graph (not shared)");

    private String label;

    GraphSharingType(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }

    public static GraphSharingType fromLinkType(LinkType linkType){
        if(linkType.equals(LinkType.BOUND_CONTEXT_REF)){
            return GraphSharingType.BOUND_GRAPH;
        }
        if(linkType.equals(LinkType.SHARED_CONTEXT_REF)){
            return GraphSharingType.SHARED_GRAPH;
        }
        return null;
    }
}
