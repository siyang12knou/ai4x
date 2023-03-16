package com.kailoslab.ai4x.py4spring;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai4x.py4spring")
@Setter
public class Py4SpringProperties {
    public static final String DEFAULT_ADDRESS_NAME = "127.0.0.1";
    public static final int DEFAULT_SPRING_PORT = 25333;
    public static final int DEFAULT_PYTHON_PORT = 25334;

    private final String springAddressName;
    private final String pythonAddressName;
    private final Integer springPort;
    private final Integer pythonPort;
    private final String authToken;
    private final Boolean convert;
    private final String pythonDirectory;

    public Py4SpringProperties(String springAddressName,
                               String pythonAddressName,
                               Integer springPort,
                               Integer pythonPort,
                               String authToken,
                               Boolean convert,
                               String pythonDirectory) {
        this.springAddressName = springAddressName;
        this.pythonAddressName = pythonAddressName;
        this.springPort = springPort;
        this.pythonPort = pythonPort;
        this.authToken = authToken;
        this.convert = convert;
        this.pythonDirectory = pythonDirectory;
    }

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

    public Boolean isConvert() {
        return convert != null && convert;
    }

    public String getPythonDirectory() {
        return pythonDirectory;
    }
}
