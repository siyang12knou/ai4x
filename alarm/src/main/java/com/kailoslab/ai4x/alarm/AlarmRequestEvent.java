package com.kailoslab.ai4x.alarm;

import org.springframework.context.ApplicationEvent;

import java.io.Serial;

public class AlarmRequestEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String domain;
    private Object before;

    public AlarmRequestEvent(String domain, Object entity) {
        super(entity);
        this.domain = domain;
    }

    public AlarmRequestEvent(String domain, Object before, Object entity) {
        super(entity);
        this.domain = domain;
        this.before = before;
    }

    public String getDomain() {
        return domain;
    }

    public Object getEntity() {
        return getSource();
    }

    public Object getBefore() {
        return before;
    }

}
