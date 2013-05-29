package com.zibea.parser.core.workers;

import java.util.concurrent.ThreadFactory;

/**
 * @author: Mikhail Bragin
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
