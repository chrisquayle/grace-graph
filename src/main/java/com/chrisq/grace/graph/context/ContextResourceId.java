package com.chrisq.grace.graph.context;

import com.chrisq.grace.graph.resource.ResourceException;
import com.chrisq.grace.graph.resource.ResourceScope;
import com.chrisq.grace.graph.resource.ResourceType;

import java.io.Serializable;
import java.util.Comparator;

public class ContextResourceId implements Comparable<ContextResourceId>, Serializable {
    private String scope;
    private String componentPart;
    private String individualIdPart;
    private ResourceType resourceType;
    public ContextResourceId() {
    }

    public ContextResourceId(String resourceId) {
        try {
            if(resourceId == null || resourceId.isEmpty() || resourceId.equalsIgnoreCase("null")){
                throw new TouchContextException("Null or empty resource ID");
            }
            String[] parts = resourceId.split(":");
            this.scope = parts[0];
            this.resourceType = parts[1] != null ? ResourceType.fromShortType(parts[1]) : null;
            this.componentPart = parts[2];
            if (parts.length == 4) {
                this.individualIdPart = parts[3];
            }
            this.getId();

        } catch (Exception e) {
            throw new ResourceException("Could not construct a valid resource from the provided resource ID: " +
                    resourceId + ". URN Format: {scope}:{type}:{component}:[{id}] Error: " + e.getMessage(), e);
        }
    }

    public ContextResourceId(String scopePart, ResourceType resourceType, String componentPart, String individualIdPart) {
        this.scope = scopePart;
        this.resourceType = resourceType;
        this.componentPart = componentPart;
        this.individualIdPart = individualIdPart;
        // Call the validator
        this.getId();
    }

    public String getId() {

        String urn;
        if (this.scope == null || this.getScope().isEmpty()) {
            throw new TouchContextException("Invalid resource: Null or empty resource scope");
        }
        if (this.componentPart == null || this.getComponentPart().isEmpty()) {
            throw new TouchContextException("Invalid resource: Null or empty component part");
        }
        if (this.individualIdPart == null) {
            urn = String.join(":", this.getScope(), this.getResourceType().getId(), this.getComponentPart());
        } else {
            urn = String.join(":", this.getScope(), this.getResourceType().getId(), this.getComponentPart(), this.getIndividualIdPart());
        }
        urn = urn.toLowerCase();
        if (!isValid(urn)) {
            throw new TouchContextException("Invalid context resource: " + urn + ". Scope part must be between 1 and 4 alpha characters, " +
                    "type must be between 1 and 4 alpha characters, " +
                    "component part can only include alpha characters, square brackets, numbers and hyphens. " +
                    "Individual ID part can also include underscores and dots. " +
                    "Individual ID part is optional and is used for individual data resources. URN Format: {scope}:{type}:{component}:[{id}]");
        }
        return urn;
    }

    public String getDotDelimitedId(){
        return this.getId().replace(":", ".");
    }

    // True if the component and type matches (regardless of scope or whether there is an individual ID)
    private boolean componentPathsMatch(String resourceId) {
        if(!isValid(resourceId)) return false;
        ContextResourceId compare = new ContextResourceId(resourceId);
        boolean typeMatch = this.getResourceType() == compare.getResourceType();
        boolean componentMatch = this.getComponentPart().equalsIgnoreCase(compare.getComponentPart());
        return typeMatch && componentMatch;
    }

    // True if the resource is a component (not an individual) and the paths match (except for scope)
    public boolean componentsMatch(String resourceId) {
        if(!isValid(resourceId)) return false;
        ContextResourceId compare = new ContextResourceId(resourceId);
        return this.isComponent() && compare.isComponent() && componentPathsMatch(resourceId);
    }

    /**
     * True if the resources are both individuals and the context path matches (regardless of scope)
     */
    private boolean individualMatches(String resourceId) {
        if(!isValid(resourceId)) return false;
        if(componentPathsMatch(resourceId)) {
            ContextResourceId compare = new ContextResourceId(resourceId);
            if (this.isIndividual() && compare.isIndividual()) {
                return this.individualIdsMatch(compare.individualIdPart);
            }
        }
        return false;
    }

    /**
     * True if the given resource is that same as this one (regardless of scope)
     */
    public boolean resourceMatches(String resourceId){
        if(!isValid(resourceId)) return false;
        if(this.individualMatches(resourceId)){
            return true;
        }
        if(this.getResourceType() == ResourceType.CONTEXT && componentPathsMatch(resourceId)){
            // Contexts are treated as matching if the component is the same (The individual ID represents a 'usage' tag in this case) TODO: Expand on this
            return true;
        }
        if(this.componentsMatch(resourceId)){
            // For all other cases, assume that the comparison is against two components (aka Containers)
            ContextResourceId compare = new ContextResourceId(resourceId);
            if(this.isComponent() && compare.isComponent()){
                return this.componentsMatch(resourceId);
            }
        }
        return false;
    }

    // True if the resource represents an individual item (not 'abstract')
    private boolean isIndividual() {
        return this.individualIdPart != null && !this.individualIdPart.isEmpty();
    }

    // True if the resource represents a component (like a container for individuals)
    private boolean isComponent() {
        return this.individualIdPart == null || this.individualIdPart.isEmpty();
    }

    private Boolean individualIdsMatch(String individualId) {
        return this.getIndividualIdPart() == null
                && individualId == null
                || this.getIndividualIdPart() != null
                && individualId != null
                && this.getIndividualIdPart()
                .equalsIgnoreCase(individualId);
    }

    // Underride the scope to be the base default - useful for when we need to check for base and specialised versions
    public String getBaseId() {
        ContextResourceId scoped = new ContextResourceId(this.getId());
        scoped.setScope(ResourceScope.RESOURCE_SCOPE_DEFAULT);
        return scoped.getId();
    }

    // True if this is a base resource (not specialised)
    public boolean isBaseResource(){
        return this.scope.equalsIgnoreCase(ResourceScope.RESOURCE_SCOPE_DEFAULT);
    }

    public static boolean isValid(String resourceId) {
        // Validate the entire URN
        String touchIdPattern = "^([a-z]{1,4}+):([a-z]{1,4}+):([a-z0-9\\-\\[\\]\\#]+)(:[a-z0-9\\-\\[\\]\\_\\.\\#]+)?$";
        return resourceId.toLowerCase().matches(touchIdPattern);
    }

    public static String getScopedResource(String scope, String resourceId) {
        if (resourceId == null) return null;
        ContextResourceId res = new ContextResourceId(resourceId);
        res.setScope(scope);
        return res.getId();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getComponentPart() {
        return componentPart;
    }

    public void setComponentPart(String componentPart) {
        this.componentPart = componentPart;
    }

    public String getIndividualIdPart() {
        return individualIdPart;
    }

    public void setIndividualIdPart(String individualIdPart) {
        this.individualIdPart = individualIdPart;
    }

    @Override
    public String toString() {
        return this.getId();
    }

    public static Comparator<ContextResourceId> componentScopeComparator =
            Comparator.comparing(ContextResourceId::getComponentPart)
            .thenComparing(ContextResourceId::getScope)
            .thenComparing(ContextResourceId::getIndividualIdPart);

    @Override
    public int compareTo(ContextResourceId o) {
        int i = this.componentPart.compareTo(o.componentPart);
        if (i != 0) return i;

        i = this.scope.compareTo(o.scope);
        if (i != 0) return i;

        if(this.individualIdPart != null && o.individualIdPart != null) {
            return this.individualIdPart.compareTo(o.individualIdPart);
        }
        return 0;
    }
}
