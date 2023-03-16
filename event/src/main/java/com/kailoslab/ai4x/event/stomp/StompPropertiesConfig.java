package com.kailoslab.ai4x.event.stomp;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StompProperties.class)
public class StompPropertiesConfig {
}
