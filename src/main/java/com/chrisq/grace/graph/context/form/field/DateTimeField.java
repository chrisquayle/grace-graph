package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

public class DateTimeField extends AbstractField {
    protected DateTimeField(){super();}
    public DateTimeField(ContextResourceId resourceId) {
        super(resourceId, NodeType.DATETIME_STRING);
    }

//    @Override
//    public Map<String, Object> toOptionMap() {
//        Map<String, Object> options = super.toOptionMap();
//        options.put("type", "date");
//        options.put("manualEntry", true);
//        // We will use standard ISO 8601 format for all locales
//        options.put("dateFormat", "YYYY-MM-DD");
//        Map<String, Object> picker = new LinkedHashMap<>();
//        options.put("picker", picker);
//        picker.put("locale", LocaleContextHolder.getLocale().getLanguage());
//        return options;
//    }
}
