package com.kailoslab.ai4x.batch.exception;

public class BatchException extends Exception{
    public BatchException() {
        super();
    }

    public BatchException(String msg) {
        super(msg);
    }

    public BatchException(Throwable cause) {
        super(cause);
    }

    public BatchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
