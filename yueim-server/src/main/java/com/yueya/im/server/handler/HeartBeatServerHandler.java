package com.yueya.im.server.handler;

import com.yueya.im.common.api.ImContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yueya.im.common.api.ImContext.IM_CONTEXT_KEY;
import static com.yueya.im.common.constant.ImInfo.CHECK_TIMES;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                ImContext context = ctx.channel().attr(IM_CONTEXT_KEY).get();
                if(context!=null){
                    int count = context.addIdleNum();
                    if(count > CHECK_TIMES){
                        logger.info("用户:{},终端:{}心跳超时,断开连接",context.getSession().getUserId(),context.getSession().getDevice());
                        context.disConnect();
                    }
                }else{
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
