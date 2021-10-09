package com.chrisq.grace.graph.content;

import com.chrisq.grace.graph.Link;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.TouchContextException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by cquayle on 6/8/2017.
 */
@SuppressWarnings("UnusedAssignment")
public class ContentLink extends Link {
    private String contentGraphId = null;
    private Link contextLink = null;

    protected ContentLink(){super();}
    public ContentLink(String contentGraphId, ContentNode source, ContentNode target, int sortIndex) {
        super(source, target);
        this.setSortIndex(sortIndex);
        this.contentGraphId = contentGraphId;
        this.setVersionId(UUID.randomUUID());
    }

    public ContentLink(UUID versionId, String contentGraphId, ContentNode source, ContentNode target, int sortIndex) {
        super(source, target);
        this.setSortIndex(sortIndex);
        this.contentGraphId = contentGraphId;
        this.setVersionId(versionId);
    }

    @Override
    public ContextResourceId getContextResourceId() {
        if(contextLink != null){
            return contextLink.getContextResourceId();
        };
        return null;
    }

    public Link getContextLink() {
        return contextLink;
    }

    public void setContextLink(Link contextLink) throws TouchContextException {
        if (contextLink != null) {
            if(!contextLink.getId().equalsIgnoreCase(this.getContextLinkId())) {
                throw new TouchContextException("Provided ContextLink " + contextLink.getId() + " does not match the ContentLink" + this.getContextLinkId());
            }
            this.contextLink = contextLink;
        }
    }

    public String getContextLinkId() {
        if (this.contextLink != null) {
            return this.contextLink.getId();
        }
        String derivedSourceContextLinkId = ((ContentNode)this.getSource()).getContextId();
        String derivedTargetContextLinkId = ((ContentNode)this.getTarget()).getContextId();
        return derivedSourceContextLinkId + "/" + derivedTargetContextLinkId;
    }

    public String getValueAsString() {
        ContentNode target = (ContentNode)super.getTarget();
        if(target == null || target.getValue() == null) return null;
        Object value = target.getValue();
        String out = value.toString();
        // Add formatters for other types here....
        if(value instanceof LocalDate){
            out = ((LocalDate)value).format(DateTimeFormatter.ISO_DATE);
        }
        return out;
    }

    @Override
    public String toString() {
        return "ContentLink{" +
                "Id='" + this.getId() + '\'' +
                ", contextLink=" + contextLink +
                ", sortIndex=" + getSortIndex() +
                '}';
    }
}