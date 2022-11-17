package com.kailoslab.ai4x.commons.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ReadingConverter
public class StringToMapConverter implements Converter<String, Map<String, Object>> {

    TypeReference<HashMap<String,Object>> typeRef
            = new TypeReference() {};

    @Override
    public Map<String, Object> convert(String jsonString) {
        Map<String, Object> jsonData;
        try {
            jsonData = Constants.JSON_MAPPER.readValue(jsonString, typeRef);
        } catch (JsonProcessingException ex) {
            log.error("Cannot convert to JSONObject: " + jsonString, ex);
            jsonData = new HashMap<>();
        }
        return jsonData;
    }
}