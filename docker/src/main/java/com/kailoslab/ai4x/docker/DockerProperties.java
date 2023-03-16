package com.kailoslab.ai4x.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai4x.docker")
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DockerProperties {

    private String host;
    private Boolean tls;
    private String tlsCert;
    private Integer max;
    private Integer timeout;
    private Registry registry;

    public String getHost() {
        return host == null ? "tcp://localhost:2376": host;
    }

    public Boolean getTls() {
        return tls != null && tls;
    }

    public String getTlsCert() {
        return tlsCert;
    }

    public Integer getMax() {
        return max == null ? 100 : max;
    }

    public Integer getTimeout() {
        return timeout == null ? 30 : timeout;
    }

    public Registry getRegistry() {
        return registry == null ? new Registry() : registry;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Registry {
        private String username;
        private String password;
        private String email;
        private String url;
    }
}
