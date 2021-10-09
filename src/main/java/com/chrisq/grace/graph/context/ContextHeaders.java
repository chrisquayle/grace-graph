package com.chrisq.grace.graph.context;

/**
 * Created by cquayle on 5/31/2017.
 */
public class ContextHeaders {

    public enum FieldEnum {
        CONTEXT_HEADERS("base:tcxf:context-headers"),
        CONTEXT_ID("base:tcxf:context-id"),
        CONTENT_ID("base:tcxf:content-id"),
        HEADER_TEXT("base:tcxf:content-header");

        private String contextId;

        FieldEnum(String contextId) {
            this.contextId = contextId;
        }

        public ContextResourceId getContextId() {
            return new ContextResourceId(contextId);
        }
    }
}
