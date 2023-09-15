package com.kailoslab.ai4x.user.config;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.service.LogService;
import com.kailoslab.ai4x.user.data.dto.SessionInfoDto;
import com.kailoslab.ai4x.utils.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final SessionInfoDto sessionInfoDto;
    private final LogService logService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.onAuthenticationFailure(request, response, exception);
        sessionInfoDto.clear();
        logService.warn(Action.login, "login failed", exception, Constants.SYSTEM_ID, request);
    }
}