package com.solab.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory for Executors that will name threads with the given prefix.
 *
 * @author Enrique Zamudio
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(0);
    private final String prefix;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, String.format("%s-%d", prefix, count.incrementAndGet()));
    }

}
