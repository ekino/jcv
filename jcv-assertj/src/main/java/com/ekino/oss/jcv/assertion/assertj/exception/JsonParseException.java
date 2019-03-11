package com.ekino.oss.jcv.assertion.assertj.exception;

/**
 * A runtime exception wrapper to handle silently json parsing exceptions.
 */
public class JsonParseException extends RuntimeException {

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
