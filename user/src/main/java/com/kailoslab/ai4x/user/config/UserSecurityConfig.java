package com.kailoslab.ai4x.user.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@RequiredArgsConstructor
public class UserSecurityConfig extends VaadinWebSecurity {

    // The secret is stored in application-secret.yml by default.
    // Never commit the secret into version control; each environment should have
    // its own secret.
    @Value("${kailoslab.auth.secret}")
    private String authSecret;

    @Value("${server.servlet.session.cookie.name:JSESSIONID}")
    private String sessionName;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(new RequestMatcher[]{new AntPathRequestMatcher("/images/*.*")}).permitAll());

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(new RequestMatcher[]{new AntPathRequestMatcher("/line-awesome/**/*.svg")}).permitAll());

        super.configure(http);

        http.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        setLoginView(http, "/login");
        setStatelessAuthentication(http, new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256),
                "com.kailoslab");
    }

}
