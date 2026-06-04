package com.datacancha.agent.exception;

public class ScoutingException extends RuntimeException {
    public ScoutingException(String message) {
        super(message);
    }

    public ScoutingException(String message, Throwable cause) {
        super(message, cause);
    }
}