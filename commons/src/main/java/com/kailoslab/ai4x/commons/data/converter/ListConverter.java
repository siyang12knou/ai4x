package com.kailoslab.ai4x.commons.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
@Slf4j
public class ListConverter implements AttributeConverter<List<Object>, String> {

    @Override
    public String convertToDatabaseColumn(List jsonData) {
        if(jsonData == null || jsonData.isEmpty()) {
            return null;
        }

        try {
            return Constants.JSON_MAPPER.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert to a String: " + jsonData);
            return "[]";
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(String jsonString) {
        if(jsonString == null || StringUtils.isEmpty(jsonString)) {
            return null;
        }

        List<Object> jsonData;
        try {
            jsonData = Constants.JSON_MAPPER.readValue(jsonString, new TypeReference<>() {});
        } catch (JsonProcessingException ex) {
            log.error("Cannot convert to a List: " + jsonString, ex);
            jsonData = new ArrayList<>();
        }
        return jsonData;
    }
}