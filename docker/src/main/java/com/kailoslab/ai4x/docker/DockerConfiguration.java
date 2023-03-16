package com.kailoslab.ai4x.docker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties(DockerProperties.class)
public class DockerConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "ai4x.docker", name = "host")
    public DockerService dockerService(DockerProperties properties) {
        return new DockerService(properties);
    }
}
