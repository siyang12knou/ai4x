package com.kailoslab.ai4x.py4spring;

import com.kailoslab.ai4x.py4spring.controller.Py4SpringDispatcher;
import com.kailoslab.ai4x.py4spring.controller.Py4SpringRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "ai4x.py4spring.enabled", havingValue = "true")
@Import({ Py4SpringRestController.class })
@EnableConfigurationProperties(Py4SpringProperties.class)
public class Py4SpringConfiguration {

    @Bean
    public Py4SpringDispatcher py4SpringDispatcher() {
        log.info("register py4SpringDispatcher");
        return new Py4SpringDispatcher();
    }

    @Bean
    public Py4SpringService py4SpringService(ApplicationContext applicationContext, Py4SpringProperties properties) {
        log.info("register py4SpringService");
        return new Py4SpringService(applicationContext, py4SpringDispatcher(), properties);
    }
}
