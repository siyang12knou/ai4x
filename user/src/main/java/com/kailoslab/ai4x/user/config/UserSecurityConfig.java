package com.kailoslab.ai4x.user.config;

import com.kailoslab.ai4x.commons.utils.Constants;
import com.kailoslab.ai4x.user.controller.UserController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Value("${server.servlet.session.cookie.name:JSESSIONID}")
    private String sessionName;

    @Autowired(required = false)
    public UserSecurityConfig(AuthSuccessHandler authSuccessHandler, AuthFailureHandler authFailureHandler) {
        this.authSuccessHandler = authSuccessHandler;
        this.authFailureHandler = authFailureHandler;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(getIgnoringPath());
    }

    protected String[] getIgnoringPath() {
        return new String[]{UserController.PATH};
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return setHttpSecurity(http).build();
    }

    protected HttpSecurity setHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> {
            try {
                auth.antMatchers(Constants.PATH_API_PREFIX + "/**").authenticated().anyRequest().permitAll()

                        .and().formLogin().loginPage("/login").loginProcessingUrl("/login")
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)

                        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").deleteCookies(sessionName)

                        .and().exceptionHandling().accessDeniedPage("/403")

                        .and().csrf().disable();
            } catch (Exception e) {
                log.error("Cannot set a security configuration.");
            }
        }).httpBasic().disable();

        return http;
    }
}