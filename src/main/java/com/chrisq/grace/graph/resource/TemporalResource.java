package com.chrisq.grace.graph.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.chrisq.grace.graph.Temporal;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Created by cquayle on 5/31/2017.
 * Modified June 2018 to implement the temporal object pattern
 * based on https://martinfowler.com/eaaDev/TemporalObject.html
 * <p>
 * This allows the object to contain all versions of its localized title and description
 * The version values returned are based on the current values of the createdUtc and Locale properties
 * <p>
 * All resources in Touch inherit from this temporal object - so they are all versioned internally
 */
public class TemporalResource extends StandardResource implements Temporal {
    // AsAtUtc represents a snapshot at which the resource will be renedered.
    // In order to ensure that the default is always ahead of any properies
    // we will set it to be 100 years from now. This should return
    // the latest version of the graph by default
    private Instant asAtUtc = TemporalResource.getAsAtUtcDefault();
    private Locale locale = Locale.US;
    // Internal list to hold all versions of this resource
    private List<ResourceVersion> versions = new LinkedList<>();

    public TemporalResource() {
        super();
    }

    public TemporalResource(String id) {
        super(id);
    }

    public TemporalResource(TemporalResource fromResource) {
        super(fromResource.getId());
        super.setCreatedUtc(fromResource.getCreatedUtc());
        super.setCreatedByUserId(fromResource.getCreatedByUserId());
        super.setState(fromResource.getState());
        this.locale = fromResource.getLocale();
        this.asAtUtc = fromResource.getAsAtUtc();
        this.versions = fromResource.getVersions();
    }

    public TemporalResource(String id, String title, String description) {
        this(id);
        this.addVersion(Locale.US, title, description, "", Instant.now(), ResourceState.PRIVATE);
    }

    public TemporalResource(String id, String title, String description, Locale locale, String createdByUserId, Instant createdUtc, ResourceState state) {
        this(id);
        this.locale = locale;
        this.asAtUtc = createdUtc;
        this.addVersion(locale, title, description, createdByUserId, createdUtc, state);
    }

    public static Instant getAsAtUtcDefault(){
        return Instant.now().plus(Duration.ofDays(36525));
    }

    /***
     * Return a standard (non-temporal) resource
     * @return
     */
    public StandardResource toStandardResource() {
        StandardResource unversioned = new StandardResource();
        this.cloneResourceFields(unversioned);
        return unversioned;
    }

    // Add a new verison in en_US locale
    public void addVersion(String title) {
        ResourceVersion version = new ResourceVersion(
                UUID.randomUUID(), super.getId(), title, title, Locale.US, super.getCreatedByUserId(), Instant.now(), ResourceState.PRIVATE
        );
        this.addVersion(version);
    }

    // Add a new version in en_US locale
    public void addVersion(String title, String description) {
        ResourceVersion version = new ResourceVersion(
                UUID.randomUUID(), super.getId(), title, description, Locale.US, super.getCreatedByUserId(), Instant.now(), ResourceState.PRIVATE
        );
        this.addVersion(version);
    }

    public ResourceVersion addVersion(Locale locale, String title, String description, String createdByUserId, Instant createdUtc, ResourceState state) {
        ResourceVersion version = new ResourceVersion(
                UUID.randomUUID(), super.getId(), title, description, locale, createdByUserId, createdUtc, state
        );
        this.addVersion(version);
        return version;
    }

    public ResourceVersion addVersion(ResourceVersion resourceVersion) {
        if (resourceVersion == null) return null;
        if(resourceVersion.getTitle().equalsIgnoreCase(this.getId())) return null;
        if (resourceVersion.getVersionId() == null) {
            resourceVersion.setVersionId(UUID.randomUUID());
        }
        if (super.getId() == null) {
            super.setId(resourceVersion.getId());
        }
        if (!resourceVersion.getContextResourceId().resourceMatches(super.getId())) {
            throw new ResourceException("Resource version cannot be added. The version provided ("
                    + resourceVersion.getId() + ") does not have the same resource ID as the temporal property (" + super.getId() + ")");
        }
        this.versions.add(resourceVersion);
        // Ensure that the versions list is sorted by create time descending
        versions.sort(new Comparator<ResourceVersion>() {
            @Override
            public int compare(ResourceVersion o1, ResourceVersion o2) {
                if (o1.getCreatedUtc().isAfter(o2.getCreatedUtc())) {
                    return -1;
                }
                return 1;
            }
        });
        return resourceVersion;
    }

    @JsonIgnore
    public ResourceVersion getVersion() {
        // Traverse all versions of the form and return the first that is applicable
        if (versions != null) {
            for (ResourceVersion version : versions) {
                // Get the last version before the given date (add a few millis to make sure it captures the date if its the same
                if (versions.size() == 1 || version.getCreatedUtc().isBefore(this.asAtUtc)) {
                    if (version.getLocale().equals(this.locale)) {
                        return version;
                    }
                }
            }
        }
        return null;
    }

//    public ResourceVersion getVersion(UUID versionId) {
//        Optional<ResourceVersion> result = this.versions.stream().filter(v -> v.getVersionId().equals(versionId)).findFirst();
//        return result.orElse(null);
//    }

    @Override
    public String getTitle() {
        ResourceVersion version = this.getVersion();
        if (version == null) {
            return this.getId();
        }
        super.setTitle(version.getTitle());
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        ResourceVersion version = this.getVersion();
        if (version == null || !version.getTitle().equalsIgnoreCase(title)) {
            if (title != null && !title.isEmpty() && !title.equalsIgnoreCase(this.getId())) {
                String description = null;
                if(version != null && version.getDescription() != null) description = version.getDescription();
                this.addVersion(this.locale, title, description, this.getCreatedByUserId(), this.getAsAtUtc(), this.getState());
            }
        }
    }

    @Override
    public String getDescription() {
        ResourceVersion version = this.getVersion();
        if (version == null) {
            return super.getDescription();
        }
        return version.getDescription();
    }

    @Override
    public void setDescription(String description) {
        ResourceVersion version = this.getVersion();
        if (version != null && description != null && !description.equalsIgnoreCase(this.getId())) {
            version.setDescription(description);
        }
    }

    @Override
    // Locale can vary by version so just return the default
    public Locale getLocale() {
        return this.locale;
    }

    public List<ResourceVersion> getVersions() {
        return versions;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setVersions(List<ResourceVersion> versions) {
        this.versions = versions;
    }

    public Instant getAsAtUtc() {
        return asAtUtc;
    }

    public void setAsAtUtc(Instant asAtUtc) {
        this.asAtUtc = asAtUtc;
    }
}