package com.chrisq.grace.graph;

/**
 *
 */
public enum NodeType {
    OBJECT("object", null, "Object", "com.chrisq.grace.graph.context.form.field.ObjectField", false),
    TEXT_STRING("string", null, "Text", "com.chrisq.grace.graph.context.form.field.TextField", true),
    NUMBER("number", null, "Number", "com.chrisq.grace.graph.context.form.field.NumberField", true),
    BOOLEAN("boolean", null, "Boolean", "com.chrisq.grace.graph.context.form.field.BooleanField", true),
    DATE_STRING("string", "date", "Date", "com.chrisq.grace.graph.context.form.field.DateField", true),
    TIME_STRING("string", "time", "Time", "com.chrisq.grace.graph.context.form.field.TimeField", true),
    DATETIME_STRING("string", "date-time", "Date-Time", "com.chrisq.grace.graph.context.form.field.DateTimeField", true),
    URI_STRING("string", "uri", "Link (URI)", "com.chrisq.grace.graph.context.form.field.TextField", true),
    EMAIL_STRING("string", "email", "Email Address", "com.chrisq.grace.graph.context.form.field.EmailField", true),
    CONTEXT("ref", null, "Form", "com.chrisq.grace.graph.context.form.field.ContextReference", false),
    CONTEXT_ARRAY("array", null, "Form Array", "com.chrisq.grace.graph.context.form.field.ContextArray", false);

    private String schemaType = null;
    private String schemaFormat = null;
    private String label = null;
    private String defaultImplementationClass = null;
    private boolean customizable = false;

    NodeType(String schemaType, String schemaFormat, String label, String defaultImplementationClass, boolean customizable) {
        this.schemaType = schemaType;
        this.schemaFormat = schemaFormat;
        this.label = label;
        this.defaultImplementationClass = defaultImplementationClass;
        this.customizable = customizable;
    }

    public String getSchemaType() {
        return this.schemaType;
    }

    public String getSchemaFormat() {
        return schemaFormat;
    }

    public String getLabel() {
        return label;
    }

    public String getDefaultImplementationClass() {
        return defaultImplementationClass;
    }

    public boolean isArray() {
        return this.schemaType.equals("array");
    }

    public boolean isCustomizable() {
        return customizable;
    }

    @Override
    public String toString() {
        return "[" + this.name() + "]";
    }
}
