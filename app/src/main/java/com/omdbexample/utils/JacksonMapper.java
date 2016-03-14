package com.omdbexample.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public class JacksonMapper {
    private ObjectMapper objectMapper;
    private JsonFactory jsonFactory;
    private static JacksonMapper instance;

    private JacksonMapper() {
        objectMapper = new ObjectMapper();
        jsonFactory = objectMapper.getFactory();
    }

    public static JacksonMapper getInstance() {
        if (instance == null)
            instance = new JacksonMapper();
        return instance;
    }


    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }
}






