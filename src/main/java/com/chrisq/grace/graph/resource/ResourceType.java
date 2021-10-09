package com.chrisq.grace.graph.resource;

public enum ResourceType {
    APP_LABEL("tlbl", "Application Label"),
    APP_CONTENT("tctn", "Application Content"),
    CONTEXT("tcx", "Context"),
    CONTEXT_FIELD("tcxf", "Context Field"),
    DATA("td", "Data Structure"),
    DATA_RESOURCE("tdrs", "Datalist Resource");

    private String label;
    private String id;

    ResourceType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    // Deserialization helper
    public static ResourceType fromShortType(String shortType) {
        for (ResourceType type : ResourceType.values()) {
            if (type.id.equalsIgnoreCase(shortType)) {
                return type;
            }
        }
        return null;
    }

    public String getId(){ return id; }

    public String getLabel() {
        return label;
    }
}
