package com.yueya.im.server.handler;

import com.yueya.im.common.api.BusinessInfoProvider;
import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.server.actors.WebSocketDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.yueya.im.common.constant.ImInfo.HEARTBEAT_IDLE;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

        private WebSocketDispatcher webSocketDispatcher = new WebSocketDispatcher();
        private EventExecutorGroup executors = new DefaultEventExecutorGroup(16);
        private boolean hearBeat;
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new WebSocketServerCompressionHandler());
            WebSocketServerHandler handler = new WebSocketServerHandler();
            handler.setDispatcher(webSocketDispatcher);
            if(isHearBeat()){
                pipeline.addLast(new IdleStateHandler(HEARTBEAT_IDLE,0,0, TimeUnit.SECONDS));
            }
            pipeline.addLast(executors,handler);
            pipeline.addLast(new HeartBeatServerHandler());
        }

        public void setHandlers(ConcurrentHashMap<String, CmdHandler> handlerMap, BusinessInfoProvider provider) {
            webSocketDispatcher.setHandlers(handlerMap);
            webSocketDispatcher.setProvider(provider);
        }

    public boolean isHearBeat() {
        return hearBeat;
    }

    public void setHearBeat(boolean hearBeat) {
        this.hearBeat = hearBeat;
    }
}
