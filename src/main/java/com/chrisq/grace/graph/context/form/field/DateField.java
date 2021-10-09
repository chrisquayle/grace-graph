package com.chrisq.grace.graph.context.form.field;

import com.chrisq.grace.graph.NodeType;
import com.chrisq.grace.graph.context.ContextResourceId;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DateField extends AbstractField {
    private String dateFormat = "DD/MM/YYYY";
    private Integer maxOffsetDaysFromNow = null;
    private Integer minOffsetDaysFromNow = null;

    protected DateField(){super();}
    public DateField(ContextResourceId resourceId) {
        super(resourceId, NodeType.DATE_STRING);
    }

    @Override
    public Map<String, Object> toOptionMap() {
        Map<String, Object> options = super.toOptionMap();
        options.put("type", "date");
        options.put("placeholder", dateFormat);
        options.put("manualEntry", false);
        // We will use standard ISO 8601 format for all locales
        options.put("dateFormat", dateFormat);
        Map<String, Object> picker = new LinkedHashMap<>();
        options.put("picker", picker);
        picker.put("locale", Locale.US.getLanguage());
        picker.put("viewMode", "days");
        if (minOffsetDaysFromNow != null) {
            picker.put("minDate", LocalDate.now().minusDays(minOffsetDaysFromNow).format(DateTimeFormatter.ISO_DATE));
        }
        if(maxOffsetDaysFromNow != null) {
            picker.put("maxDate", LocalDate.now().plusDays(maxOffsetDaysFromNow).format(DateTimeFormatter.ISO_DATE));
        }
        return options;
    }

    public Integer getMaxOffsetDays() {
        return maxOffsetDaysFromNow;
    }

    public Integer getMaxOffsetDaysFromNow() {
        return maxOffsetDaysFromNow;
    }

    public void setMaxOffsetDaysFromNow(Integer maxOffsetDaysFromNow) {
        this.maxOffsetDaysFromNow = maxOffsetDaysFromNow;
    }

    public Integer getMinOffsetDaysFromNow() {
        return minOffsetDaysFromNow;
    }

    public void setMinOffsetDaysFromNow(Integer minOffsetDaysFromNow) {
        this.minOffsetDaysFromNow = minOffsetDaysFromNow;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public LocalDate getLocalDate(String localDate){
        // TODO: Localize dates and restrict formats based on compatible moment.js strings. Java Time formats do not map to moment.js - see https://momentjs.com/docs/#/parsing/string-format/
        try {
            return LocalDate.parse(localDate, DateTimeFormatter.ofPattern("d/MM/yyyy")); // this.dateFormat));
        } catch(DateTimeParseException e){
            return LocalDate.parse(localDate); // this.dateFormat));
        }
    }

    public LocalDate getISODate(String isoDate){
        return LocalDate.parse(isoDate);
    }


    public String getLocalDateString(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
    }
}
