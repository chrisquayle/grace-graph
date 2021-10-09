package com.chrisq.grace.graph.context;

import com.chrisq.grace.graph.*;
import com.chrisq.grace.graph.context.form.field.ContextReference;
import com.chrisq.grace.graph.context.form.field.Field;
import com.chrisq.grace.graph.resource.ResourceScope;

public class ContextLink extends Link {
    protected ContextLink(){super();}
    public ContextLink(Field source, Field target) {
        super(source, target);
        // Map sharing context into the link
        if(target instanceof ContextReference){
            GraphSharingType sharingType = ((ContextReference) target).getGraphSharingType();
            if(sharingType.equals(GraphSharingType.SHARED_GRAPH)){
                this.setLinkType(LinkType.SHARED_CONTEXT_REF);
            }
            if(sharingType.equals(GraphSharingType.BOUND_GRAPH)){
                this.setLinkType(LinkType.BOUND_CONTEXT_REF);
            }
        }
    }

    // Return true if the link inherits or represents a property in the given scope
    public boolean isInContext(String linkId) {
        String[] parts = linkId.split("/");
        boolean sourceMatch = this.getSource().getContextResourceId().resourceMatches(parts[0]);
        boolean targetsMatch = this.getTarget().getContextResourceId().resourceMatches(parts[1]);
        return sourceMatch && targetsMatch;
    }

    // Coerce the naming to represent the base link ID - with any overriding scope removed
    public static String getBaseLinkId(String sourceNodeId, String targetNodeId) {
        ContextResourceId source = new ContextResourceId(sourceNodeId);
        ContextResourceId target = new ContextResourceId(targetNodeId);
        source.setScope(ResourceScope.RESOURCE_SCOPE_DEFAULT);
        target.setScope(ResourceScope.RESOURCE_SCOPE_DEFAULT);
        GraphNode sourceNode = new GraphNode(source.getId(), NodeType.OBJECT);
        GraphNode targetNode = new GraphNode(target.getId(), NodeType.OBJECT);
        Link baseLink = new Link(sourceNode, targetNode);
        return baseLink.getId();
    }

    @Override
    public Field getSource() {
        return (Field)super.getSource();
    }

    @Override
    public Field getTarget() {
        return (Field)super.getTarget();
    }

}
