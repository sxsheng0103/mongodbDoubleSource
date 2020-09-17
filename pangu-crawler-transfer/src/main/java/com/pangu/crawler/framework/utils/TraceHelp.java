package com.pangu.crawler.framework.utils;

import java.util.concurrent.atomic.AtomicLong;

public class TraceHelp {

    private static final AtomicLong atom = new AtomicLong(0);

    public static String uuid() {
        String uuid = Thread.currentThread().getName();
        uuid = uuid.toLowerCase();
        uuid = uuid.replaceAll("\\s+", "-");
        uuid = uuid.replaceAll("\\.", "_");
        uuid += "." + System.currentTimeMillis();
        uuid += "." + atom.getAndIncrement();
        return uuid;
    }
}
