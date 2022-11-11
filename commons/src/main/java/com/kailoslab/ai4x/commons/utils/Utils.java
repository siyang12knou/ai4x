package com.kailoslab.ai4x.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class Utils {
    /**
     * Yaml 파일을 읽는다.
     *
     * @param resource Yaml 파일에 대한 {@link Resource}
     * @return Yaml 파일에 대한 프로퍼티 객체
     */
    public static Properties loadYaml(Resource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource);
            factory.afterPropertiesSet();

            return factory.getObject();
        } catch (IllegalStateException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof FileNotFoundException) throw (FileNotFoundException) cause;
            throw ex;
        }
    }

    public static String stringifyJson(Object object) {
        try {
            return Constants.JSON_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert object to string." + object);
            throw new IllegalArgumentException("Cannot convert object to string." + object);
        }
    }

    public static Map<String, Object> mapJson(Object object) {
        return Constants.JSON_MAPPER.convertValue(object, new TypeReference<Map<String, Object>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
    }

    public static <T> T parseJson(String string) {
        try {
            return Constants.JSON_MAPPER.readValue(string, new TypeReference<>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Cannot convert string to object " + string);
            throw new IllegalArgumentException("Cannot convert string" + string);
        }
    }

    public static HashMap<String, String> convert(Properties prop) {
        return prop.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue()),
                        (prev, next) -> next, HashMap::new
                ));
    }

    public static String toCamelCase(String text) {
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static String toFirstLowerCase(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }
}
