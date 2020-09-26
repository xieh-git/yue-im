package com.yueya.im.server.handler;

import com.yueya.im.server.actors.TcpDispatcher;
import com.yueya.im.server.base.TcpPact;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpHandler extends SimpleChannelInboundHandler<Object> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private TcpDispatcher tcpDispatcher;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        TcpPact pact = (TcpPact) msg;
        handleTextMsg(pact.getMsgType(),pact.getContent(),ctx);
    }

    public void setDispatcher(TcpDispatcher tcpDispatcher) {
        this.tcpDispatcher = tcpDispatcher;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        tcpDispatcher.handlerRemoved(ctx);
    }

    private void handleTextMsg(int code, byte[] content, ChannelHandlerContext ctx) {
        tcpDispatcher.handle(code,content,ctx);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("socket err",cause);
        ctx.close();
    }
}
