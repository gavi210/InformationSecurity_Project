package it.unibz.examproject.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonOperations {
    public static <T> T getObject(String jsonString, Class<T> inputClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, inputClass);
    }
}

