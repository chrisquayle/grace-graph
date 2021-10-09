package com.chrisq.grace.graph.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.chrisq.grace.graph.tree.Branch;
import com.chrisq.grace.graph.tree.treeview.TreeViewSerializer;

public class Mapper {

    // Single static object mapper for basic serialization
    private static ObjectMapper objectMapper;

    /***
     * Helper methof to get a mapper instance when autowiring is not available
     * @return
     */
    public static ObjectMapper getObjectMapper()
    {
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
            // Some other custom configuration for supporting Java 8 features
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            // Custom treeview serializer
            SimpleModule touchModule = new SimpleModule();
            touchModule.addSerializer(Branch.class, new TreeViewSerializer());
            objectMapper.registerModule(touchModule);
        }
        return objectMapper;
    }
}
