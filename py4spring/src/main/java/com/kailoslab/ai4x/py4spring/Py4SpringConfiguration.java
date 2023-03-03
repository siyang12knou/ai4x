package com.kailoslab.ai4x.py4spring;

import com.kailoslab.ai4x.py4spring.controller.Py4SpringDispatcher;
import com.kailoslab.ai4x.py4spring.controller.Py4SpringRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.kailoslab.ai4x.py4spring.Py4SpringPythonProxyRepository.PYTHON_PROXY_REPOSITORY;

@Configuration
@Slf4j
@Import({ Py4SpringRestController.class })
@EnableConfigurationProperties(Py4SpringProperties.class)
public class Py4SpringConfiguration {

    @Bean
    public Py4SpringDispatcher py4SpringDispatcher() {
        log.info("register py4SpringDispatcher");
        return new Py4SpringDispatcher();
    }

    @Bean(PYTHON_PROXY_REPOSITORY)
    public Py4SpringPythonProxyRepository py4SpringPythonProxyRepository() {
        log.info("register py4SpringPythonProxyRepository");
        return new Py4SpringPythonProxyRepository();
    }

    @Bean
    public Py4SpringService py4SpringService(ApplicationContext applicationContext, Py4SpringProperties properties) {
        log.info("register py4SpringService");
        return new Py4SpringService(applicationContext, py4SpringDispatcher(), properties, py4SpringPythonProxyRepository());
    }
}
