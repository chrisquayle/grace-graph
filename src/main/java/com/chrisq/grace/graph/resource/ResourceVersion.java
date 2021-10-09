package com.chrisq.grace.graph.resource;

import com.chrisq.grace.graph.Resource;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class ResourceVersion extends StandardResource {
    private UUID versionId = null; //UUID.randomUUID();

    // Default constructor is required for serialization
    protected ResourceVersion(){super();}
    public ResourceVersion(String resourceId){
        super(resourceId);
    }
    public ResourceVersion(Resource resource){
        this(UUID.randomUUID(), resource.getId(), resource.getTitle(), resource.getDescription(), resource.getLocale(), resource.getCreatedByUserId(), resource.getCreatedUtc(), resource.getState());
    }
    public ResourceVersion(UUID versionId, String resourceId, String title, String description, Locale locale, String createdByUserId, Instant createdUtc, ResourceState state) {
        super(resourceId);
        this.versionId = versionId;
        super.setTitle(title);
        super.setDescription(description);
        super.setLocale(locale);
        super.setCreatedByUserId(createdByUserId);
        super.setCreatedUtc(createdUtc);
        super.setState(state);
    }

    public UUID getVersionId() {
        return versionId;
    }

    public void setVersionId(UUID versionId) {
        this.versionId = versionId;
    }


    @Override
    public String toString() {
        return "ResourceVersion{" +
                "versionId=" + versionId +
                super.toString() +
                '}';
    }
}
