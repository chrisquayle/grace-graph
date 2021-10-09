package com.chrisq.grace.graph.context.form;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chrisq.grace.graph.context.ContextResourceId;
import com.chrisq.grace.graph.context.form.field.TextField;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class TextFieldTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void fieldMetaSchemaTest(){
        TextField field = new TextField(new ContextResourceId("base:tcxf:test:field"));
        field.setDefaultValue("Test Field");
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println("TEXT FIELD META-SCHEMA");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(field.toMetaSchemaMap()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail();
        }
    }

}