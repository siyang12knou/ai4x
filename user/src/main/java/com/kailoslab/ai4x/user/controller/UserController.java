package com.kailoslab.ai4x.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
public class UserController {
    public static final String PATH = "/user";

    @GetMapping(PATH + "/current")
    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No login"
            );
        }
    }

    @GetMapping(PATH + "/loginSuccess")
    public String getLoginSuccess() {
        return getCurrentUserId();
    }

    @GetMapping("/login")
    public ResponseEntity afterLogin() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ?
                ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
    }
}