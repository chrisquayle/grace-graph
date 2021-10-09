package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.GraphSharingType;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.resource.ResourceException;

import java.util.List;
import java.util.Map;

public class ContextArray extends AbstractField {
    private Integer minItems = 0;
    private Integer maxItems = 1;
    private Boolean uniqueItems = false;
    private ContextReference contextReference = null;


    @Override
    public NodeType getNodeType() {
        return NodeType.CONTEXT_ARRAY;
    }

    protected ContextArray() {
        super();
    }

    public ContextArray(ContextResourceId resourceId, ContextReference contextReference) throws ResourceException {
        super(resourceId, NodeType.CONTEXT_ARRAY);
        this.contextReference = contextReference;
        this.setIsArray(true);
    }

    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        putIfNotNull(node, "minItems", this.minItems);
        putIfNotNull(node, "maxItems", this.maxItems);
        putIfNotNull(node, "uniqueItems", this.uniqueItems);
        if(this.maxItems == 1){
            node.remove("title");
            node.remove("description");
        }
        return node;
    }

    @Override
    public Map<String, Object> toOptionMap() {
        Map<String, Object> options = super.toOptionMap();

        options.put("toolbarSticky", true);
        options.put("toolbarStyle", "link");
        options.put("actionbarStyle", "bottom");
        Map<String, Object> toolbar = putNewMap(options, "toolbar");
        toolbar.put("showLabels", false);
        List<Map<String, Object>> toolbarActions = putNewList(toolbar, "actions");

        Map<String, Object> actionbar = putNewMap(options, "actionbar");
        actionbar.put("showLabels", false);
        actionbar.put("enabled", true);

        options.put("hideToolbarWithChildren", false);
        toolbarActions.clear();

        List<Map<String, Object>> actionBarActions = putNewList(actionbar,"actions");
        Map<String, Object> actionBarAdd = putNewMap(actionBarActions);
        actionBarAdd.put("action", "add");
        actionBarAdd.put("enabled", false);
        Map<String, Object> actionBarRemove = putNewMap(actionBarActions);
        actionBarRemove.put("action", "remove");
        actionBarRemove.put("enabled", false);
        Map<String, Object> actionBarUp = putNewMap(actionBarActions);
        actionBarUp.put("action", "up");
        actionBarUp.put("enabled", false);
        Map<String, Object> actionBarDown = putNewMap(actionBarActions);
        actionBarDown.put("action", "down");
        actionBarDown.put("enabled", false);

        if (this.contextReference != null && this.contextReference.getGraphSharingType().equals(GraphSharingType.SHARED_GRAPH)) {
            // Override the add action to implement custom search
//            if(this.maxItems > 1) {
//                Map<String, Object> customAction = putNewMap(toolbarActions);
//                customAction.put("action", "add");
//                customAction.put("enabled", true);
//                customAction.put("iconClass", "glyphicon glyphicon-plus");
//                //customAction.put("click", "<script>function(){$('#contactModal').modal()}</script>");
//            }
            //actionBarAdd.put("click", "<script>alert('REF ADD CLICKED')</script>");
            actionBarAdd.put("iconClass", "glyphicon glyphicon-plus");

            // Add an edit button to the actionbar
//            Map<String, Object> actionBarEdit = putNewMap(actionBarActions);
//            actionBarEdit.put("action", "edit");
//            actionBarEdit.put("enabled", true);
//            actionBarEdit.put("iconClass", "glyphicon glyphicon-edit");
            //actionBarAdd.put("click", "<script>function(){$('#contactModal').modal()}</script>");
        }
        if (this.maxItems > 1) {
            actionBarAdd.put("enabled", true);
            actionBarRemove.put("action", "remove");
            actionBarRemove.put("enabled", true);
            actionBarUp.put("action", "up");
            actionBarUp.put("enabled", true);
            actionBarDown.put("action", "down");
            actionBarDown.put("enabled", true);
        }

        return options;
    }

    public ContextReference getContextReference() {
        return contextReference;
    }

    public void setContextReference(ContextReference contextReference) {
        this.contextReference = contextReference;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    @Override
    public String toString() {
        return "ContextArray{" + super.toString() +
                "; minItems=" + minItems +
                ", maxItems=" + maxItems +
                ", uniqueItems=" + uniqueItems +
                ", contextReference=" + contextReference +
                '}';
    }
}
