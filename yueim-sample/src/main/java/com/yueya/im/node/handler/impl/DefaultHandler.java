package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.common.api.ImContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler implements CmdHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preMessage(int msgType, byte[] message, long msgId) {
        return true;
    }

    @Override
    public void onMessage(ImContext ctx, int msgType, byte[] message,long msgId) {
    }

    @Override
    public void postMessage(ImContext ctx,int msgType, byte[] message,long msgId) {
    }
}
