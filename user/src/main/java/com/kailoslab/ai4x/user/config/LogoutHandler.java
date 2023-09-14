package com.kailoslab.ai4x.user.config;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.service.LogService;
import com.kailoslab.ai4x.utils.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements ApplicationListener<SessionDestroyedEvent> {

    private LogService logService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onLogoutSuccess(request, response, authentication);
        if(authentication.getPrincipal() instanceof UserDetails userDetails) {
            logService.info(Action.logout, "Successful logout", userDetails.getUsername());
        }
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        String userName = Constants.SYSTEM_ID;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            userName = userDetails.getUsername();
        }

        logService.info(Action.destroy_session, "Destroy a session.", userName);
    }
}
