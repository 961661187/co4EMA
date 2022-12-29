package com.lasat.dsdco.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {
    private final ObjectMapper objectMapper;

    private ObjectMapperSingleton(){
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    private static class Holder {
        private static final ObjectMapperSingleton objectMapperSingleton = new ObjectMapperSingleton();
    }

    public static ObjectMapperSingleton getInstance() {
        return Holder.objectMapperSingleton;
    }
}
