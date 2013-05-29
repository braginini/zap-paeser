package com.zibea.parser.core.exception;

/**
 * @author: Mikhail Bragin
 */
public class BatchException extends Exception {

    public BatchException(Throwable cause) {
        super(cause);
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
