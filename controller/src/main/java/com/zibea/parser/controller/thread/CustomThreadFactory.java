package com.zibea.parser.controller.thread;

import java.util.concurrent.ThreadFactory;

/**
 * @author: mbragin
 */
public class CustomThreadFactory implements ThreadFactory {

    private int counter = 0;
    private String prefix = "";

    public CustomThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, prefix + "-" + counter++);
    }
}
