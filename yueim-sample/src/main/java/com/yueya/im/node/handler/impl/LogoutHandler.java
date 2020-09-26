package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.common.api.ImContext;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandler implements CmdHandler {


    @Override
    public boolean preMessage(int msgType, byte[] message,long msgId) {
        return true;
    }

    @Override
    public void onMessage(ImContext ctx, int msgType, byte[] message, long msgId) {
    }

    @Override
    public void postMessage(ImContext ctx,int msgType, byte[] message,long msgId) {
    }
}
