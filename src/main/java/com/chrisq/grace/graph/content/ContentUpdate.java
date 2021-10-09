package com.chrisq.grace.graph.content;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by cquayle on 5/31/2017.
 */
public class ContentUpdate {
    private UUID id = UUID.randomUUID();
    private String touchPointId;
    private String primaryUpdateResourceId = FieldEnum.PRIMARY_UPDATE_DEFAULT.getResourceId();
    private String secondaryUpdateResourceId = FieldEnum.SECONDARY_UPDATE_DEFAULT.getResourceId();
    private String comment = "";
    private Boolean sendNotification = false;
    private String trackingPageUrl = null;
    private String createdByUserId = "admin";
    private Instant createdUtc = Instant.now();

    public enum FieldEnum {
        PRIMARY_UPDATE_FIELD("base:tcxf:update:primary-update"),
        SECONDARY_UPDATE_FIELD("base:tcxf:update:secondary-update"),
        UPDATE_DATALIST_ID("base:td:touchpoint-update"),
        PRIMARY_UPDATE_DEFAULT("base:tdrs:update:created"),
        SECONDARY_UPDATE_DEFAULT("base:tdrs:update:created-default");

        private String resourceId;

        FieldEnum(String resourceId){
            this.resourceId = resourceId;
        }

        public String getResourceId() {
            return resourceId;
        }
    }

    public ContentUpdate() { }

    public String getTouchPointId() {
        return touchPointId;
    }

    public void setTouchPointId(String touchPointId) {
        this.touchPointId = touchPointId;
    }

    public String getPrimaryUpdateResourceId() {
        return primaryUpdateResourceId;
    }

    public void setPrimaryUpdateResourceId(String primaryUpdateResourceId) {
        this.primaryUpdateResourceId = primaryUpdateResourceId;
    }

    public String getSecondaryUpdateResourceId() {
        return secondaryUpdateResourceId;
    }

    public void setSecondaryUpdateResourceId(String secondaryUpdateResourceId) {
        this.secondaryUpdateResourceId = secondaryUpdateResourceId;
    }

    @JsonIgnore
    public String getTrackingLink(){
        if(this.trackingPageUrl != null && !this.trackingPageUrl.isEmpty()){
            return this.trackingPageUrl + '/' + this.id.toString();

        }
        return null;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Instant getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(Instant createdUtc) {
        this.createdUtc = createdUtc;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

}
