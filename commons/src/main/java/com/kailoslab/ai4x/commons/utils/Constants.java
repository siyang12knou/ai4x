package com.kailoslab.ai4x.commons.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Constants {
    ObjectMapper JSON_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    String SYSTEM_ID = "system";
    String PATH_API_PREFIX = "/api";
    String DEFAULT_GROUP_ID = "0000000000";
    int ORDINAL_START = 0;
}
