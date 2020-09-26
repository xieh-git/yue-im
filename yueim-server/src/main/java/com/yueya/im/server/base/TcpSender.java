package com.yueya.im.server.base;

import com.yueya.im.common.api.ImContext;
import com.yueya.im.server.handler.TcpMsgDecoder;
import io.netty.buffer.ByteBuf;

public class TcpSender extends DefaultSender{
    @Override
    public void writeMsg(ImContext context, int msgType, String content) {
        ByteBuf message =  TcpMsgDecoder.encode(msgType,content);
        context.getChannelHandlerContext()
                .channel()
                .write(message);
    }
}
