package com.kailoslab.ai4x.py4spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kailoslab.ai4x.py4spring.Py4SpringException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

public class Py4SpringDispatcher {

    @Value("${ai4x.py4spring.path:}")
    private String prefixPath;
    private final String paramSeparator = "&";
    private final ObjectMapper mapper = json()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modules(new JavaTimeModule())
            .build();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final Map<Info, IPythonRestFunction> restFunctionMap = Collections.synchronizedMap(new TreeMap<>(
            (info1, info2) -> {
            if((antPathMatcher.isPattern(info1.route()) && antPathMatcher.isPattern(info2.route())) ||
                    (!antPathMatcher.isPattern(info1.route()) && !antPathMatcher.isPattern(info2.route()))) {
               return StringUtils.countMatches(info1.route(), AntPathMatcher.DEFAULT_PATH_SEPARATOR) -
                       StringUtils.countMatches(info2.route(), AntPathMatcher.DEFAULT_PATH_SEPARATOR);
           } else {
               return antPathMatcher.isPattern(info1.route()) ? 1 : -1;
           }
       }
    ));

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Object dispatch(HttpServletRequest request, String bodyStr) throws Py4SpringException {
        Info info = getRestInfo(request);
        if(info == null) {
            throw new Py4SpringException("Cannot find a controller: " + request.getRequestURI());
        } else {
            IPythonRestFunction restController = restFunctionMap.get(info);
            Map<String, String> pathVariables = getPathVariables(request, info);
            Map<String, String> headers = getHttpHeaders(request);
            Map<String, List<String>> query = convertParameterToMap(request.getQueryString());
            Object body = convertStringToObject(bodyStr);
            Map<String, Object> httpInfo = new HashMap<>(3);
            httpInfo.put("path_variables", pathVariables);
            httpInfo.put("headers", headers);
            httpInfo.put("query", query);
            httpInfo.put("body", body);

            try {
                return convertStringToObject(restController.call(mapper.writeValueAsString(httpInfo)).toString());
            } catch (JsonProcessingException e) {
                throw new Py4SpringException("Cannot execute a controller: " + request.getRequestURI());
            }
        }
    }

    private Info getRestInfo(HttpServletRequest request) {
        String apiUri = getApiUri(request);
        for (Info info :
                restFunctionMap.keySet()) {
            if (StringUtils.equals(apiUri, info.route()) ||
                    (antPathMatcher.isPattern(info.route()) && antPathMatcher.match(info.route(), apiUri))){
                if(RequestMethod.valueOf(request.getMethod()) == info.method()) {
                    return info;
                }
            }
        }

        return null;
    }

    public Boolean registerRestFunction(IPythonRestFunction restFunction) {
        Future<Boolean> result = executor.submit(() -> {
            try {
                Info info = new Info(restFunction.getPath(), RequestMethod.valueOf(restFunction.getMethodName()));
                restFunctionMap.put(info, restFunction);
                return true;
            } catch (Throwable ex) {
                return false;
            }
        });

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public void unregisterRestFunction(String path, String methodName) {
        Info info = new Info(path, RequestMethod.valueOf(methodName));
        if(restFunctionMap.containsKey(info)) {
            restFunctionMap.remove(info);
        }
    }

    private String getApiUri(HttpServletRequest request) {
        if(StringUtils.isEmpty(prefixPath)) {
            return request.getRequestURI();
        } else {
            int index = request.getRequestURI().indexOf(prefixPath) + prefixPath.length();
            return request.getRequestURI().substring(index);
        }
    }

    private Map<String, String> getPathVariables(HttpServletRequest request, Info info) {
        if(antPathMatcher.isPattern(info.route())) {
            String apiUri = getApiUri(request);
            return antPathMatcher.extractUriTemplateVariables(info.route(), apiUri);
        } else {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> getHttpHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> requestHeaders = request.getHeaderNames();
        String headerName;
        while(requestHeaders.hasMoreElements()) {
            headerName = requestHeaders.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return headers;
    }

    private Object convertStringToObject(String str) {
        Object result = str;
        try {
            result = mapper.convertValue(mapper.readTree(str), Map.class);
        } catch (Throwable ex) {
            try {
                result = mapper.convertValue(mapper.readTree(str), List.class);
            } catch (Throwable ex1) {
                if (StringUtils.contains(str, paramSeparator)) {
                    result = convertParameterToMap(str);
                }
            }
        }

        return result;
    }
    private Map<String, List<String>> convertParameterToMap(String bodyStr) {
        Map<String, List<String>> parameters = new HashMap<>();
        if(StringUtils.isNotEmpty(bodyStr) && bodyStr.contains(paramSeparator)) {
            String[] keyValuePairs = bodyStr.split(paramSeparator);
            for (String keyValuePair : keyValuePairs) {
                String[] keyAndValue = keyValuePair.split("=", 2);

                String key = keyAndValue[0];
                String value = keyAndValue.length > 1 ? keyAndValue[1] : "";

                key = URLDecoder.decode(key, StandardCharsets.UTF_8);
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);

                parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return parameters;
    }

    record Info(String route, RequestMethod method) {}
}
