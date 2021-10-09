package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.GraphSharingType;
import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cquayle on 6/29/2017.
 */
public class ContextReference extends AbstractField {
    private GraphSharingType graphSharingType = GraphSharingType.BOUND_GRAPH;

    protected ContextReference(){super();}
    public ContextReference(ContextResourceId resourceId) {
        super(resourceId, NodeType.CONTEXT);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CONTEXT;
    }

    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        root.put("$ref", "#/definitions/" + this.getId());
        return root;
    }

    @Override
    public Map<String, Object> toOptionMap() {
        Map<String, Object> options = super.toOptionMap();
//        options.put("collapsible", true);
//        options.put("collapsed", false);
        return options;
    }

    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> metaSchemaMap = super.toMetaSchemaMap();
        Map<String, Object> metaSchemaPropertiesMap = (Map<String, Object>) metaSchemaMap.get("properties");

        LinkedHashMap<String, Object> typeMap = putNewMetaPropertyMap(metaSchemaPropertiesMap, "graphSharingType", NodeType.TEXT_STRING, GraphSharingType.SHARED_GRAPH.getLabel(), false, false);
        LinkedList typeSelect = new LinkedList();
        typeMap.put("enum", typeSelect);
        for (GraphSharingType type : GraphSharingType.values()) {
            typeSelect.add(type.name());
        }
        return metaSchemaMap;
    }

    @Override
    public Map<String, Object> toMetaOptionMap() {
        Map<String, Object> optionMap = super.toMetaOptionMap();
        Map<String, Object> fieldsMap = (Map<String, Object>) optionMap.get("fields");
        Map<String, Object> contentEntityTypeMap = putNewMap(fieldsMap, "graphSharingType");
        contentEntityTypeMap.put("type", "select");
        contentEntityTypeMap.put("default", GraphSharingType.SHARED_GRAPH.name());
        List<String> classificationLabels = putNewList(contentEntityTypeMap, "optionLabels");
        for (GraphSharingType graphSharingType : GraphSharingType.values()) {
            classificationLabels.add(graphSharingType.getLabel());
        }
        return optionMap;
    }

    public GraphSharingType getGraphSharingType() {
        return graphSharingType;
    }

    public void setGraphSharingType(GraphSharingType graphSharingType) {
        this.graphSharingType = graphSharingType;
    }
}
