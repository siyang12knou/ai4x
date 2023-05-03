package com.kailoslab.ai4x.commons.exception;

import java.util.Collections;
import java.util.List;

public class Ai4xException extends RuntimeException {

    private final List<String> messages;

    public Ai4xException() {
        super();
        this.messages = Collections.emptyList();
    }

    public Ai4xException(String message) {
        super(message);
        this.messages = Collections.singletonList(message);
    }

    public Ai4xException(String message, Throwable cause) {
        super(message, cause);
        this.messages = Collections.singletonList(message);
    }

    public Ai4xException(Throwable cause) {
        super(cause);
        this.messages = Collections.singletonList(cause.getMessage());
    }

    protected Ai4xException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.messages = Collections.singletonList(message);
    }

    public Ai4xException(List<String> messages) {
        super(messages.get(0));
        this.messages = messages;
    }
}
