package com.chrisq.grace.graph;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class GraphVersion {
    private UUID versionId = UUID.randomUUID();
    private String graphId;
    private String parentGraphId;
    private Instant versionUtc;

    public GraphVersion(){}
    public GraphVersion(String graphId, String parentContentGraphId, Instant versionUtc){

        this.graphId = graphId;
        this.parentGraphId = parentContentGraphId;
        this.versionUtc = versionUtc;
    }

    public UUID getVersionId() {
        return versionId;
    }

    public void setVersionId(UUID versionId) {
        this.versionId = versionId;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public String getParentGraphId() {
        return parentGraphId;
    }

    public void setParentGraphId(String parentContentGraphId) {
        this.parentGraphId = parentContentGraphId;
    }

    public Instant getVersionUtc() {
        return versionUtc;
    }

    public void setVersionUtc(Instant versionUtc) {
        this.versionUtc = versionUtc;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GraphVersion that = (GraphVersion) o;
        return  graphId.equalsIgnoreCase(that.graphId) &&
                parentGraphId.equalsIgnoreCase(parentGraphId) &&
                versionUtc.equals(that.versionUtc);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(graphId, parentGraphId, versionUtc);
        return result;
    }

}
