package com.kailoslab.ai4x.user.config;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.service.LogService;
import com.kailoslab.ai4x.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLoginHandler implements AuthenticationSuccessHandler, ApplicationListener<SessionCreationEvent> {

    private final LogService logService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(authentication.getPrincipal() instanceof UserDetails userDetails) {
            logService.info(Action.login, "Successful login", userDetails.getUsername(), request);
        }
    }

    @Override
    public void onApplicationEvent(SessionCreationEvent event) {
        String userName = Constants.SYSTEM_ID;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            userName = userDetails.getUsername();
        }

        logService.info(Action.create_session, "Created a session.", userName);
    }
}