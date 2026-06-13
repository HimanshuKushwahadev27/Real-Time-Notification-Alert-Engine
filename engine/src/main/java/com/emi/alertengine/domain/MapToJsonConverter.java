package com.emi.alertengine.domain;


import java.util.Map;

import org.springframework.boot.json.JsonParseException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MapToJsonConverter 
        implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Java → DB: called on save/update
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
            // Map {"name":"Himanshu"} → stored as '{"name":"Himanshu"}' in DB
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Could not serialize payload to JSON", e);
        }
    }

    // DB → Java: called on read/fetch
    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return objectMapper.readValue(dbData, 
                new TypeReference<Map<String, String>>() {});
            // '{"name":"Himanshu"}' → Map {"name":"Himanshu"}
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Could not deserialize payload from JSON", e);
        }
    }
}