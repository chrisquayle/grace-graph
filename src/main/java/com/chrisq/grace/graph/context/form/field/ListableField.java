package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.data.DataResource;
import com.chrisq.grace.graph.resource.ResourceException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
 * Base class for others that allow values to be selected from a list
 */
public class ListableField extends PatternField {
    private boolean multiValueDataList = false;
    // True if options list is to be included in the the field definition instead of accessed via URL at runtime
    private boolean localDataList = false;
    private ContextResourceId dataListGraphId = null;
    private ContextResourceId dataListId = null;
    private String dataListFilterPath = null;
    private List<DataResource> dataList = null;

    protected ListableField() {
        super();
    }

    public ListableField(ContextResourceId resourceId, NodeType fieldType) throws ResourceException {
        super(resourceId, fieldType);
    }

    @Override
    public Map<String, Object> toSchemaMap() {
        Map<String, Object> node = super.toSchemaMap();
        // Options can either be loaded from the datasource at runtime, or stored in the optionslist as resource IDs
        LinkedList<String> ids = new LinkedList<>();
        if (this.localDataList && this.dataList != null && this.dataList.size() > 0) {
            this.dataList.forEach(o -> ids.add(o.getId()));
            node.put("enum", ids);
        }
        return node;
    }

    @Override
    public Map<String, Object> toOptionMap() {
        boolean isListable = this.dataListId != null || this.dataListFilterPath != null; ;
        Map<String, Object> options = super.toOptionMap();
        options.put("emptySelectFirst", true);
        // Simple data lists are included in the field schema as an enum list
        if (this.localDataList && this.dataList != null && this.dataList.size() > 0) {
            LinkedList<String> labels = new LinkedList<>();
            this.dataList.forEach(o -> labels.add(o.getTitle()));
            options.put("optionLabels", labels);
        }
        // Remote (not shipped/local) data lists are accessed by the form via ajax calls at runtime
        // NOTE: Embedded scripts have placeholder <script></script> tags to help us to un-quote them before injecting into JS blocks
        if (!this.localDataList && this.dataListId != null && this.getDataListFilterPath() == null) {
            // This is a static remote list (not filtered by another field value)
            options.put("useDataSourceAsEnum", true);
            options.put("dataSource", "<script>getResourceList('" + this.dataListId + "')</script>");
        }
        if (!this.localDataList && this.getDataListFilterPath() != null) {
            // This is a remote list filtered by another field value. Subscriptions are setup in the client-side JS
            options.put("useDataSourceAsEnum", true);
            options.put("enum", "[]");
        }
        if (isListable) {
            options.put("type", "select");
            options.put("validate", false);
            options.put("sort", false);
            options.put("hideNone", true);
        }
        if(isListable && isMultiValueDataList()){
            options.put("multiple", true);
            // Add an element for multilist plugin options. https://github.com/davidstutz/bootstrap-multiselect
            LinkedHashMap<String, Object> multiselectPluginOptions = putNewMap(options, "multiselect");
            // allSelectedText is the text displayed if all options are selected. You can disable displaying the allSelectedText by setting it to false.
            multiselectPluginOptions.put("allSelectedText", false);
        }
        return options;
    }

    public boolean isMultiValueDataList() {
        return multiValueDataList;
    }

    public void setMultiValueDataList(boolean multiValueDataList) {
        this.multiValueDataList = multiValueDataList;
    }

    public boolean isLocalDataList() {
        return localDataList;
    }

    public void setLocalDataList(boolean localDataList) {
        this.localDataList = localDataList;
    }

    public ContextResourceId getDataListGraphId() {
        return dataListGraphId;
    }

    public void setDataListGraphId(ContextResourceId dataListGraphId) {
        this.dataListGraphId = dataListGraphId;
    }

    public ContextResourceId getDataListId() {
        return dataListId;
    }

    public void setDataListId(ContextResourceId dataListId) {
        this.dataListId = dataListId;
    }

    public String getDataListFilterPath() {
        return dataListFilterPath;
    }

    public void setDataListFilterPath(String dataListFilterPath) {
        this.dataListFilterPath = dataListFilterPath;
    }

    public List<DataResource> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataResource> dataList) {
        this.dataList = dataList;
    }

    @Override
    public Map<String, Object> toMetaSchemaMap() {
        Map<String, Object> map = super.toMetaSchemaMap();
        Map<String, Object> properties = (LinkedHashMap) map.get("properties");
        putNewMetaPropertyMap(properties, "datasource", NodeType.TEXT_STRING, null, false, true);
        putNewMetaPropertyMap(properties, "filterFieldPath", NodeType.TEXT_STRING, null, false, false);
        putNewMetaPropertyMap(properties, "multiValueDataList", NodeType.BOOLEAN, false, false, false);
        return map;
    }

    @Override
    public Map<String, Object> toMetaOptionMap() {
        Map<String, Object> map = super.toMetaOptionMap();
        LinkedHashMap<String, Object> fields = (LinkedHashMap<String, Object>) map.get("fields");
        if(dataList != null ) {
            LinkedHashMap<String, Object> datasource = putNewMap(fields, "datasource");
            putResourceBrowseButton(datasource);
        }
        return map;
    }

    @Override
    public Map<String, Object> toMetaDataMap() {
        Map<String, Object> map = super.toMetaDataMap();
        if(dataList != null) {
            putIfNotNull(map, "datasource", dataListId.getId());
        }
        putIfNotNull(map, "filterFieldPath", this.dataListFilterPath);
        map.put("multiValueDataList", this.multiValueDataList);
        return map;
    }
}