package com.zibea.parser.core.exception;

import java.io.IOException;

/**
 * @author: Mikhail Bragin
 */
public class PageNotFoundException extends IOException {

    public PageNotFoundException(String message) {
        super(message);
    }
}

