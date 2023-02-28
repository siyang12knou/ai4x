package com.kailoslab.ai4x.py4spring;

public class Py4SpringException extends Exception {
    public Py4SpringException() {
        super();
    }

    public Py4SpringException(String message) {
        super(message);
    }

    public Py4SpringException(String message, Throwable cause) {
        super(message, cause);
    }

    public Py4SpringException(Throwable cause) {
        super(cause);
    }
}
