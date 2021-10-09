package com.chrisq.grace.graph.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LocaleUtil {

    public static Locale getParsedLocale(String localeId){
        if(localeId == null) return Locale.US;
        // Need to parse the tag to ensure it is IETF BCP 47 (E.g. en-US and not en_US)
        localeId = localeId.replace("_", "-");
        return Locale.forLanguageTag(localeId);
    }

    /***
     * Generate a key to identify the local value. This is to accommodate multiple locale values in caches for the same zone
     * @param key
     * @return
     */
    public static String getLocaleBasedKey(String key){
        return key + "/" + LocaleContextHolder.getLocale().toString();
    }
}
