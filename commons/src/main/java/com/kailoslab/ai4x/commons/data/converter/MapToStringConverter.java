package com.kailoslab.ai4x.commons.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@Slf4j
@WritingConverter
public class MapToStringConverter implements Converter<Map<String, Object>, String> {

    @Override
    public String convert(Map<String, Object> jsonData) {

        try {
            return Constants.JSON_MAPPER.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert a map to a string: " + jsonData);
            return "{}";
        }
    }
}