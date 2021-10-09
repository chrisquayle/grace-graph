package com.chrisq.grace.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.chrisq.grace.graph.resource.TemporalResource;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphNode extends TemporalResource implements Node, Serializable {
    private NodeType nodeType = NodeType.TEXT_STRING;
    private boolean isArray = false;

    protected GraphNode(){super();}
    public GraphNode(TemporalResource fromResource){
        super(fromResource);
    }
    public GraphNode(String id, NodeType nodeType){
        super(id);
        this.nodeType = nodeType;
    }

    // Map utility helpers

    /**
     * Return json schema property
     */
    @Override
    @JsonIgnore
    public Map<String, Object> toSchemaMap(){
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("id", this.getId());
        putIfNotNull(root, "title", this.getTitle());
        putIfNotNull(root, "description", this.getDescription());
        putIfNotNull(root, "type", this.getNodeType().getSchemaType());
        putIfNotNull(root, "format", this.getNodeType().getSchemaFormat());
        return root;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == this) return true;
//        if (!(o instanceof Node)) {
//            return false;
//        }
//        if(!((Node) o).getNodeType().equals(this.nodeType)){
//            return false;
//        }
//
//    }

    @Override
    /**
     * Return a json formatted node
     */
    public String toString() {
       return String.join("", "{" + super.toString(),
               ", \"type\":\"", this.nodeType.toString(), "\"}");
    }

    public String toGraphJson(){
        return String.join("", "{\"id\": \"" + this.getId(),
                "\", \"name\": \"", this.getTitle(), "\", ", "\"type\":\"", this.nodeType.toString(), "\"}");
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public Boolean isArray() {
        return this.isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    // Map utility methods
    public static void putIfNotNull(Map<String, Object> map, String name, Object value){
        if(value != null){
            map.put(name, value);
        }
    }

    public static void putIfTrue(Map<String, Object> map, String name, boolean value){
        if(value){
            map.put(name, value);
        }
    }

    public static LinkedHashMap<String, Object> putNewMap(Map<String, Object> putIntoMap, String name){
        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        putIntoMap.put(name, newMap);
        return newMap;
    }

    public static LinkedHashMap<String, Object> putNewMap(List<Map<String, Object>> putIntoList){
        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        putIntoList.add(newMap);
        return newMap;
    }

    public static LinkedList putNewList(Map<String, Object> putIntoMap, String name){
        LinkedList<Object> newList = new LinkedList<>();
        putIntoMap.put(name, newList);
        return newList;
    }


}
