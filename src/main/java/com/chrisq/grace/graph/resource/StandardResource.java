package com.chrisq.grace.graph.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.chrisq.grace.graph.Resource;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.util.LocaleUtil;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.Instant;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardResource implements Resource {
    private String createdByUserId = "admin";
    private String id = null;
    private String title = "";
    private String description = "";
    private Locale locale = Locale.US;
    private ResourceState state = ResourceState.PRIVATE;
    private Instant createdUtc = Instant.now();

    // Initialise authenticated user name
    {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            this.createdByUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        }
    }

    public StandardResource() {}
    public StandardResource(String id){
        this.id = id;
    }
    
    public void cloneResourceFields(Resource toResource){
        toResource.setId(this.getId());
        toResource.setLocale(this.getLocale());
        toResource.setCreatedByUserId(this.getCreatedByUserId());
        toResource.setCreatedUtc(this.getCreatedUtc());
        toResource.setDescription(this.getDescription());
        toResource.setTitle(this.getTitle());
        toResource.setState(this.getState());
    }

    @Override
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        // if (this.id == null) {
        this.id = id;
        //     return;
        // }
        // TODO: HACK! ID should not be be overwritten! Disabling the check though to allow contentlink id to be set
    }

    @Override
    public ContextResourceId getContextResourceId() {
        if(ContextResourceId.isValid(this.id)){
            return new ContextResourceId(this.id);
        };
        return null;
    }

    @Override
    @JsonIgnore
    public ResourceType getResourceType() {
        if(this.id != null){
            return this.getContextResourceId().getResourceType();
        }
        return null;
    }

    @Override
    public String getTitle() {
        return title == "" ? id : title;
    }
    public void setTitle(String title) {
        this.title = title == null ? "NULL" : title;
    }

    @Override
    public String getDescription() {
        return description == "" ? this.getTitle() : description;
    }
    public void setDescription(String description) {
        this.description = description == null ? this.getTitle() : description;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    public void setLocale(String localeId){
        this.locale = LocaleUtil.getParsedLocale(localeId);
    }

    @Override
    public String getCreatedByUserId() {
        return this.createdByUserId;
    }
    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    @Override
    public ResourceState getState() {
        return this.state;
    }
    public void setState(ResourceState state) {
        this.state = state;
    }

    @Override
    public Instant getCreatedUtc() {
        return this.createdUtc;
    }
    public void setCreatedUtc(Instant createdUtc) {
        this.createdUtc = createdUtc;
    }

    @Override
    // Ordering is based on the context ID
    public int compareTo(Resource o) {
        return this.getContextResourceId().compareTo(o.getContextResourceId());
    }

    public static Comparator<Resource> defaultOrderComparator = Comparator.<Resource> naturalOrder();

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Resource)) {
            return false;
        }
        if(this.getId() == null) return false;
        StandardResource that = (StandardResource) o;
        return (this.getId().equals(that.getId()) &&
                this.getLocale().equals(that.getLocale()) &&
                this.getTitle().equals(that.getTitle()) &&
                this.getDescription().equals(that.getDescription())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.getLocale(), this.getTitle(), this.getDescription());
    }

    @Override
    public String toString() {
        return "StandardResource{" +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}