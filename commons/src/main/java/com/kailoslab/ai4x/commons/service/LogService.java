package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.code.Level;
import com.kailoslab.ai4x.commons.data.LogRepository;
import com.kailoslab.ai4x.commons.data.entity.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class LogService {

    private final LogRepository logRepository;

    public void info(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        info(caller, action, message, userId);
    }

    public void info(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.INFO, action, message, userId, LocalDateTime.now()));
    }

    public void warn(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId);
    }

    public void warn(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.WARN, action, message, userId, LocalDateTime.now()));
    }

    public void warn(Action action, String message, Throwable e, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId);
    }

    public void warn(String caller, Action action, String message, Throwable e, String userId) {
        logRepository.save(new Log(caller, Level.WARN, action, message + ":" + e.getMessage(), userId, LocalDateTime.now()));
    }

    public void error(Action action, String message, String userId) {
        String caller = getCallerMethodName();
        error(caller, action, message, userId);
    }

    public void error(String caller, Action action, String message, String userId) {
        logRepository.save(new Log(caller, Level.ERROR, action, message, userId, LocalDateTime.now()));
    }

    public void error(Action action, String message, Throwable e, String userId) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId);
    }

    public void error(String caller, Action action, String message, Throwable e, String userId) {
        logRepository.save(new Log(caller, Level.ERROR, action, message + ":" + e.getMessage(), userId, LocalDateTime.now()));
    }

    public void info(Action action, String message, String userId, String data) {
        String caller = getCallerMethodName();
        info(caller, action, message, userId, data);
    }

    public void info(String caller, Action action, String message, String userId, String data) {
        logRepository.save(new Log(caller, Level.INFO, action, message, userId, LocalDateTime.now(), data));
    }

    public void warn(Action action, String message, String userId, String data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId, data);
    }

    public void warn(String caller, Action action, String message, String userId, String data) {
        logRepository.save(new Log(caller, Level.WARN, action, message, userId, LocalDateTime.now(), data));
    }

    public void warn(Action action, String message, Throwable e, String userId, String data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId, data);
    }

    public void warn(String caller, Action action, String message, Throwable e, String userId, String data) {
        logRepository.save(new Log(caller, Level.WARN, action, message + ":" + e.getMessage(), userId, LocalDateTime.now(), data));
    }

    public void error(Action action, String message, String userId, String data) {
        String caller = getCallerMethodName();
        error(caller, action, message, userId, data);
    }

    public void error(String caller, Action action, String message, String userId, String data) {
        logRepository.save(new Log(caller, Level.ERROR, action, message, userId, LocalDateTime.now(), data));
    }

    public void error(Action action, String message, Throwable e, String userId, String data) {
        String caller = getCallerMethodName();
        warn(caller, action, message, userId, data);
    }

    public void error(String caller, Action action, String message, Throwable e, String userId, String data) {
        logRepository.save(new Log(caller, Level.ERROR, action, message + ":" + e.getMessage(), userId, LocalDateTime.now(), data));
    }

    private String getCallerMethodName() {
        return StackWalker.
                getInstance().
                walk(stream -> stream.skip(2).findFirst().orElse(null)).
                getMethodName();
    }
}
