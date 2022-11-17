package com.kailoslab.ai4x.commons.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@ReadingConverter
public class StringToListConverter implements Converter<String, List<Object>> {

    TypeReference<List<Object>> typeRef
            = new TypeReference() {};

    @Override
    public List<Object> convert(String jsonString) {
        List<Object> jsonData;
        try {
            jsonData = Constants.JSON_MAPPER.readValue(jsonString, typeRef);
        } catch (JsonProcessingException ex) {
            log.error("Cannot convert to a List: " + jsonString, ex);
            jsonData = new ArrayList<>();
        }
        return jsonData;
    }
}