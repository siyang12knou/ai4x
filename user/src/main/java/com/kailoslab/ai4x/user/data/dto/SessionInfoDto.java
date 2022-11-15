package com.kailoslab.ai4x.user.data.dto;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String email;
    private List<String> roles;

    public void clear() {
        id = null;
        email = null;
        roles = null;
    }

    public SessionInfoDto copy() {
        return new SessionInfoDto(id, email, roles);
    }
}