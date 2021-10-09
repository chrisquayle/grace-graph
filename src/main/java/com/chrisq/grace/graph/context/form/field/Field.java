package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.Node;

import java.util.Map;
import java.util.UUID;

public interface Field extends Node {
    UUID getVersionId();
    void setVersionId(UUID versionId);
    Object getDefaultValue();
    void setDefaultValue(Object value);
    boolean isHidden();
    void setHidden(boolean hidden);
    boolean isReadOnly();
    void setReadOnly(boolean readOnly);
    boolean isRequired();
    void setRequired(boolean required);
    boolean isSearchable();
    void setSearchable(boolean searchable);
    boolean isHeader();
    void setHeader(boolean header);
    FieldClassification getClassification();
    /***
     * Returns the JSON Schema for the field
     * @return
     */
    String toSchemaJson();
    Map<String, Object> toSchemaMap();

    /***
     * Returns the JS Options (AlpacaJS) for the field.
     * (This is Javascript intended to be injected into the page on the server.
     * Not JSON as it may have embedded function calls etc)
     * @return
     */
    String toOptionJs();
    Map<String, Object> toOptionMap();

    /***
     * Returns the JSON Schema for the definition of the field
     * @return
     */
    String toMetaSchemaJson();
    Map<String, Object> toMetaSchemaMap();

    /***
     * Returns the JSON for the AlpacaJS options for the definitions of the field
     * @return
     */
    String toMetaOptionJs();
    Map<String, Object> toMetaOptionMap();

    /***
     * Returns the JSON data for the AlpacaJS data for the definitions of the field
     * @return
     */
    String toMetaDataJson();
    Map<String, Object> toMetaDataMap();
}
