package com.chrisq.grace.graph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.chrisq.grace.graph.content.ContentLink;
import com.chrisq.grace.graph.context.ContextLink;
import com.chrisq.grace.graph.context.data.DataLink;
import com.chrisq.grace.graph.resource.TemporalResource;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * TODO: ContentUpdate file header
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContextLink.class, name = "contextLink"),
        @JsonSubTypes.Type(value = ContentLink.class, name = "contentLink"),
        @JsonSubTypes.Type(value = DataLink.class, name = "dataLink")})
public class Link extends TemporalResource implements Serializable {
    private UUID versionId = null;
    private int sortIndex = 0;
    private Node source;
    private Node target;
    private LinkType linkType = LinkType.PROPERTY;

    // Default constructor is required for serialization
    protected Link(){super();}
    public Link(Node source, Node target){
        this(source, LinkType.PROPERTY, target);
    }
    public Link(Node source, LinkType linkType, Node target){
        super(String.join("/", source.getId(), target.getId()));
        this.source = source;
        this.linkType = linkType;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Link)) {
            return false;
        }
        Link p = (Link) o;
        return ((Link) o).getId().equalsIgnoreCase(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public String toD3GraphJson(){
        return "{\"source\": \"" + this.source.getId() + "\", \"target\": \"" + this.target.getId() + "\", \"id\": \"" + super.getId() + "\"}";
    }

    public Node getSource() {
        return source;
    }

    public String getSourceId(){ return source.getId();}

    public void setSource(Node source) {
        this.source = source;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public Node getTarget() {
        return target;
    }

    public String getTargetId(){return target.getId();}

    public void setTarget(Node target) {
        this.target = target;
    }

    public UUID getVersionId() {
        return versionId;
    }

    public void setVersionId(UUID versionId) {
        this.versionId = versionId;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + super.getId() +
                '}';
    }
}
