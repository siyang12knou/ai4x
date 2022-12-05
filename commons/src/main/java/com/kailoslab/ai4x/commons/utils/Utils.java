package com.kailoslab.ai4x.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
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

    public static String snakeToCamel(String snake) {
        String[] words = snake.split("[\\W_]+");
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

    public static String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public static String toFirstLowerCase(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.length() > 1 ? text.substring(1) : "";
    }

    public static String toFirstUpperCase(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.length() > 1 ? text.substring(1) : "";
    }

    public static <T> T newInstance(Map<String, String> properties, Class<T> clazz) {
        if(ObjectUtils.isEmpty(properties)) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getFields();
            Arrays.stream(fields).forEach(field -> {
                String snakeName = Utils.camelToSnake(field.getName());
                if(StringUtils.isNotEmpty(properties.get(snakeName))) {
                    try {
                        Method setMethod = clazz.getMethod("set" + toFirstUpperCase(field.getName()));
                        Class<?>[] params = setMethod.getParameterTypes();
                        if(params.length == 1) {
                            Method valueOf = params[0].getMethod("valueOf", String.class);
                            setMethod.invoke(instance, valueOf.invoke(null, properties.get(snakeName)));
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
                }
            });

            return instance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static List<String> getScanPackages(ApplicationContext applicationContext) {
        String[] springBootAppBeanName = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
        List<String> scanPackages = new ArrayList<>(Collections.singleton("com.kailoslab.ai4x"));
        Arrays.stream(springBootAppBeanName)
            .forEach(name -> {
                Class<?> applicationClass = applicationContext.getBean(name).getClass();
                ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
                if(componentScan == null && applicationClass.getSuperclass() != null) {
                    Class<?> applicationSuperClass = applicationClass.getSuperclass();
                    componentScan = applicationSuperClass.getAnnotation(ComponentScan.class);
                }

                if(componentScan == null) {
                    scanPackages.add(applicationContext.getBean(name).getClass().getPackageName());
                } else {
                    for (Class<?> basePackageClass :
                            componentScan.basePackageClasses()) {
                        scanPackages.add(basePackageClass.getPackageName());
                    }

                    Collections.addAll(scanPackages, componentScan.basePackages());
                }
            });

        return scanPackages;
    }

    public static String getString(Object object, String... methodNames) {
        String result = null;
        for(String methodName: methodNames) {
            try {
                Method m = object.getClass().getMethod(methodName, null);
                Object obj = m.invoke(object, null);
                if(ObjectUtils.isNotEmpty(obj)) {
                    result = obj.toString();
                    break;
                }
            } catch (Throwable ignored) {
            }
        }

        return result;
    }

    public static Integer getInt(Object object, String... methodNames) {
        Integer result = 0;
        for(String methodName: methodNames) {
            try {
                Method m = object.getClass().getMethod(methodName, null);
                Object obj = m.invoke(object, null);
                if(ObjectUtils.isNotEmpty(obj)) {
                    result = Integer.parseInt(obj.toString());
                    break;
                }
            } catch (Throwable ignored) {}
        }

        return result;
    }
}
