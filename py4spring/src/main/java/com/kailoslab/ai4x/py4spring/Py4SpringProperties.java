package com.kailoslab.ai4x.py4spring;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai4x.py4spring")
@Setter
public class Py4SpringProperties {
    public static final String DEFAULT_ADDRESS_NAME = "127.0.0.1";
    public static final int DEFAULT_SPRING_PORT = 25333;
    public static final int DEFAULT_PYTHON_PORT = 25334;
    public static final String DEFAULT_AUTH_TOKEN = "py4spring";

    private String springAddressName;
    private String pythonAddressName;
    private Integer springPort;
    private Integer pythonPort;
    private String authToken;

    public String getSpringAddressName() {
        return springAddressName == null ? DEFAULT_ADDRESS_NAME : springAddressName;
    }

    public String getPythonAddressName() {
        return pythonAddressName == null ? DEFAULT_ADDRESS_NAME : pythonAddressName;
    }

    public Integer getSpringPort() {
        return springPort == null ? DEFAULT_SPRING_PORT : springPort;
    }

    public Integer getPythonPort() {
        return pythonPort == null ? DEFAULT_PYTHON_PORT : pythonPort;
    }

    public String getAuthToken() {
        return authToken == null ? DEFAULT_AUTH_TOKEN : authToken;
    }
}
