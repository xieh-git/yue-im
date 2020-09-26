package com.yueya.im.server.handler;

import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.common.api.ImContext;

import static com.yueya.im.common.constant.YueMsgCode.MSG_HEART_BEAT;

public class DefaultBeatHander implements CmdHandler {
    @Override
    public boolean preMessage(int msgType, byte[] message, long msgId) {
        return true;
    }

    @Override
    public void onMessage(ImContext ctx, int msgType, byte[] message, long msgId) {
        if(msgType == MSG_HEART_BEAT){
            ctx.sendToClient(msgType,"");
        }
    }

    @Override
    public void postMessage(ImContext ctx, int msgType, byte[] message, long msgId) {

    }
}
