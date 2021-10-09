package com.chrisq.grace.graph.context.data;

import com.chrisq.grace.graph.context.TouchContextException;
import com.chrisq.grace.graph.context.form.field.AbstractField;
import com.chrisq.grace.graph.resource.ResourceType;
import com.chrisq.grace.graph.resource.TemporalResource;

public class DataResource extends AbstractField {
    // Empty contructor required for serialisation etc
    protected DataResource() {
        super();
    }

    public DataResource(String id) {
        this(new TemporalResource(id));
    }

    public DataResource(TemporalResource fromResource) {
        super(fromResource);
        if (this.getContextResourceId().getResourceType() != ResourceType.DATA_RESOURCE && this.getContextResourceId().getResourceType() != ResourceType.DATA) {
            throw new TouchContextException("Invalid type. Cannot create data resource. Expected td or tdrs (Given: " + fromResource.getId() + ")");
        }
    }

}
