package com.yueya.im.common.runner;

import java.util.concurrent.ThreadFactory;

public class PostThreadFactory implements ThreadFactory {
    private final String  PREFIX = "postMessageThread";
    private int counter;
    @Override
    public Thread newThread(Runnable r) {
        Thread  thread =new Thread(r,PREFIX+"-"+counter);
        counter++;
        return thread;
    }
}
