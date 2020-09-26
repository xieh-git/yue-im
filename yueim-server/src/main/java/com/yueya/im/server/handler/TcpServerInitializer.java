package com.yueya.im.server.handler;


import com.yueya.im.common.api.BusinessInfoProvider;
import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.server.actors.TcpDispatcher;
import com.yueya.im.server.base.TcpSender;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.yueya.im.common.constant.ImInfo.HEARTBEAT_IDLE;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {

    private TcpDispatcher tcpDispatcher = new TcpDispatcher();
    private EventExecutorGroup executors = new DefaultEventExecutorGroup(16);
    private boolean hearBeat;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new TcpMsgDecoder());
        TcpHandler tcpHandler = new TcpHandler();
        tcpHandler.setDispatcher(tcpDispatcher);
        if(isHearBeat()){
            pipeline.addLast(new IdleStateHandler(HEARTBEAT_IDLE,0,0, TimeUnit.SECONDS));
        }
        pipeline.addLast(executors,tcpHandler);
        pipeline.addLast(new HeartBeatServerHandler());
    }

    public void setHandlers(ConcurrentHashMap<String, CmdHandler> handlerMap, BusinessInfoProvider provider) {
        tcpDispatcher.setSender(new TcpSender());
        tcpDispatcher.setHandlers(handlerMap);
        tcpDispatcher.setProvider(provider);
    }

    public boolean isHearBeat() {
        return hearBeat;
    }

    public void setHearBeat(boolean hearBeat) {
        this.hearBeat = hearBeat;
    }
}
