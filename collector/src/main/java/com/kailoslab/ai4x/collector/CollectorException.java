package com.kailoslab.ai4x.collector;

public class CollectorException extends Exception {
    public CollectorException(String message) {
        super(message);
    }

    public CollectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
