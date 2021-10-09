package com.chrisq.grace.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.resource.ResourceState;
import com.chrisq.grace.graph.resource.ResourceType;

import java.time.Instant;
import java.util.Locale;

public interface Resource extends Comparable<Resource> {
    /***
     * The id will match the contextResourceId if the implementation is a context resource, otherwise it will be a unique ID for a content instance
     */
    String getId();
    void setId(String id);
    /***
     *All resources in touch will map to a context
     */
    String getTitle();
    void setTitle(String title);

    String getDescription();
    void setDescription(String description);

    Locale getLocale();
    void setLocale(Locale locale);

    Instant getCreatedUtc();
    void setCreatedUtc(Instant createdUtc);

    String getCreatedByUserId();
    void setCreatedByUserId(String createdByUserId);

    ResourceType getResourceType();

    ResourceState getState();
    void setState(ResourceState state);

    @JsonIgnore
    ContextResourceId getContextResourceId();
    
}
