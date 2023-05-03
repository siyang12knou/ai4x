package com.kailoslab.ai4x.commons.controller;

import com.kailoslab.ai4x.commons.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URISyntaxException;
import java.util.UUID;

//@RestController
@RequiredArgsConstructor
@Slf4j
public class ProxyController {

    @Value("${ai4x.proxy.domain:}")
    private String domain;

    private final ProxyService service;

    @RequestMapping("/**")
    public ResponseEntity<String> sendRequestToSPM(@RequestBody(required = false) String body,
                                                   HttpMethod method, HttpServletRequest request, HttpServletResponse response)
            throws URISyntaxException {
        if(StringUtils.isEmpty(domain)) {
            log.error("No setting 'ai4x.proxy.domain'");
            throw new IllegalStateException("No setting 'ai4x.proxy.domain'");
        } else {
            return service.processProxyRequest(body, method, request, response, UUID.randomUUID().toString());
        }
    }
}