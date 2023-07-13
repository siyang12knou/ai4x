package com.kailoslab.ai4x.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

public interface Constants {
    ObjectMapper JSON_MAPPER = json()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modules(new JavaTimeModule())
            .build().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    String SYSTEM_ID = "system";
    String SYSTEM_GROUP_ID = "system";
    String PATH_API_PREFIX = "/api";
    String PATH_API_AI4X_PREFIX = PATH_API_PREFIX + "/ai4x";
    String DEFAULT_GROUP_ID = "0000000000";
    int ORDINAL_START = 0;

    String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";
    String SLASH = "/";
    String COLON = ":";
    String DOT = ".";
    String COMMA = ",";

    NumberFormat nf = NumberFormat.getInstance();
    String dfStr = "yyyy-MM-dd";
    String dtfStr = dfStr + " HH:mm:ss";

    String broadcastTopic = "/broadcast";
    List<String> localhost = Arrays.asList("localhost", "127.0.0.1", "0:0:0:0:0:0:0:1");

    Random random = new Random();
}
