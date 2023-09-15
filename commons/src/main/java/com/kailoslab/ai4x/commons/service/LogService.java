package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.code.Length;
import com.kailoslab.ai4x.commons.code.Level;
import com.kailoslab.ai4x.commons.data.LogRepository;
import com.kailoslab.ai4x.commons.data.entity.Log;
import com.kailoslab.ai4x.utils.Ai4xUtils;
import com.kailoslab.ai4x.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class LogService {

    private final String separator = "::";
    private final LogRepository logRepository;

    public void info(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        info(caller, action, message, userId);
    }

    public void info(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.INFO, action, message, "", userId, LocalDateTime.now()));
    }

    public void warn(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId);
    }

    public void warn(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.WARN, action, message, "", userId, LocalDateTime.now()));
    }

    public void warn(Action action, String message, Throwable e, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, e, userId);
    }

    public void warn(String caller, Action action, String message, Throwable e, String userId) {
        logRepository.save(new Log(caller, Level.WARN, action, message + separator + e.getMessage(), "", userId, LocalDateTime.now()));
    }

    public void error(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        error(caller, action, message, userId);
    }

    public void error(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.ERROR, action, message, "", userId, LocalDateTime.now()));
    }

    public void error(Action action, String message, Throwable e, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, e, userId);
    }

    public void error(String caller, Action action, String message, Throwable e, String userId) {
        logRepository.save(new Log(caller, Level.ERROR, action, message + separator + e.getMessage(), "", userId, LocalDateTime.now()));
    }

    public void info(Action action, String message, String userId, Object... data) {
        String caller = getCallerMethodName();
        info(caller, action, message, userId, data);
    }

    public void info(String caller, Action action, String message, String userId, Object...  data) {
        logRepository.save(new Log(caller, Level.INFO, action, message, getClientIp(data), userId, LocalDateTime.now(), convertDataToString(data)));
    }

    public void warn(Action action, String message, String userId, Object...  data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId, data);
    }

    public void warn(String caller, Action action, String message, String userId, Object...  data) {
        logRepository.save(new Log(caller, Level.WARN, action, message, getClientIp(data), userId, LocalDateTime.now(), convertDataToString(data)));
    }

    public void warn(Action action, String message, Throwable e, String userId, Object...  data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, e, userId, data);
    }

    public void warn(String caller, Action action, String message, Throwable e, String userId, Object...  data) {
        logRepository.save(new Log(caller, Level.WARN, action, message + separator + e.getMessage(), getClientIp(data), userId, LocalDateTime.now(), convertDataToString(data)));
    }

    public void error(Action action, String message, String userId, Object...  data) {
        String caller = getCallerMethodName();
        error(caller, action, message, userId, data);
    }

    public void error(String caller, Action action, String message, String userId, Object...  data) {
        logRepository.save(new Log(caller, Level.ERROR, action, message, getClientIp(data), userId, LocalDateTime.now(), convertDataToString(data)));
    }

    public void error(Action action, String message, Throwable e, String userId, Object...  data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, e, userId, data);
    }

    public void error(String caller, Action action, String message, Throwable e, String userId, Object...  data) {
        logRepository.save(new Log(caller, Level.ERROR, action, message + separator + e.getMessage(), getClientIp(data), userId, LocalDateTime.now(), convertDataToString(data)));
    }

    private String getCallerMethodName() {
        StackWalker.StackFrame stackFrame = StackWalker.
                getInstance().
                walk(stream -> stream.skip(2).findFirst().orElse(null));

        String name = stackFrame.getClassName().replaceAll("\\B\\w+(\\.[a-z])","$1") + Constants.COLON + stackFrame.getMethodName();
        if(name.length() > Length.path) {
            name = StringUtils.substring(name, name.length() - Length.path, name.length());
        }

        return name;
    }

    private String getClientIp(Object... dataArray) {
        Optional<Object> requestOptional = Arrays.stream(dataArray).filter(datum -> datum instanceof HttpServletRequest).findFirst();
        AtomicReference<String> ipAddress = new AtomicReference<>("");
        requestOptional.ifPresent(requestObj -> {
            HttpServletRequest request = (HttpServletRequest) requestObj;
            ipAddress.set(request.getHeader("X-FORWARDED-FOR"));
            if (ipAddress.get() == null) {
                ipAddress.set(request.getRemoteAddr());
            }
        });

        return ipAddress.get();
    }

    private String convertDataToString(Object... dataArray) {
        List<Object> data = Arrays.stream(dataArray).filter(datum -> !(datum instanceof HttpServletRequest)).toList();
        if(data.isEmpty()) return null;
        else return Ai4xUtils.convertToString(data);
    }
}
