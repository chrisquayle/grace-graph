package com.chrisq.grace.graph.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.chrisq.grace.graph.context.data.DataGraph;
import com.chrisq.grace.graph.resource.StandardResource;
import com.chrisq.grace.graph.util.Mapper;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;

public class ContextDefinition {
    private String sourceUrl = "";
    private String statepointId = "";
    @JsonInclude()
    private Instant asAtUtc = Instant.now();
    private LinkedList<StandardResource> contextResourceCollection = new LinkedList<>();
    private LinkedList<DataGraph> contextDataGraphCollection = new LinkedList<>();

    public void addResource(StandardResource resource) {
        this.contextResourceCollection.add(resource);
    }

    public LinkedList<StandardResource> getContextResourceCollection() {
        return contextResourceCollection;
    }

    public LinkedList<DataGraph> getContextDataGraphCollection() {
        return contextDataGraphCollection;
    }

    public Instant getAsAtUtc() {
        return asAtUtc;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getStatepointId() {
        return statepointId;
    }

    public void setStatepointId(String statepointId) {
        this.statepointId = statepointId;
    }

    public static ContextDefinition fromJson(String json) {
        try {
            return Mapper.getObjectMapper().readValue(json, ContextDefinition.class);
        } catch (IOException e) {
            throw new TouchContextException("Could not deserialize context from JSON: " + e.toString());
        }
    }

    public String toJson(){
        try {
            return Mapper.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new TouchContextException("Could not serialize context to JSON: " + e.toString());
        }
    }

    @Override
    public String toString() {
        return "ContextDefinition{" +
                "sourceUrl='" + sourceUrl + '\'' +
                ", statepointId='" + statepointId + '\'' +
                ", asAtUtc=" + asAtUtc +
                '}';
    }
}
