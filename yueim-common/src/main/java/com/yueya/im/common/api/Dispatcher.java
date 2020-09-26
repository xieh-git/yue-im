package com.yueya.im.common.api;

import com.yueya.im.common.runner.PostThreadFactory;
import com.yueya.im.common.util.SnowflakeIdWorker;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static com.yueya.im.common.api.ImContext.IM_CONTEXT_KEY;

public abstract class Dispatcher {
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected ConcurrentHashMap<String, CmdHandler> handlerMap;
    protected BusinessInfoProvider infoProvider;
    protected ThreadFactory threadFactory = new PostThreadFactory();
    protected ExecutorService executor = new ThreadPoolExecutor(10,50,0L,TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue(1024),threadFactory,new ThreadPoolExecutor.AbortPolicy());
    protected MsgSender sender;
    public boolean handle(int msgType, byte[] content, ChannelHandlerContext ctx) {
        try {
            String key = msgType+"";
            if(handlerMap.containsKey(key)){
                long msgId = SnowflakeIdWorker.generateId();
                CmdHandler handler = handlerMap.get(key);
                boolean flag = handler.preMessage(msgType,content,msgId);
                if(!flag) {
                    return false;
                }
                ImContext context = getImContext(ctx);
                if(context == null){
                    context = new ImContext(ctx,sender);
                    ctx.channel().attr(IM_CONTEXT_KEY).set(context);
                }
                //重置心跳超时次数
                context.resetIdleNum();
                handler.onMessage(context,msgType,content,msgId);
                ImContext finalContext = context;
                executor.execute(()->handler.postMessage(finalContext,msgType,content,msgId));

            }else {
                logger.warn("该消息类型没有对应的消息处理器:{}",msgType);
            }
        } catch (Exception e) {
            logger.error("handler err",e);
            return false;
        }
        return true;
    }
    private ImContext getImContext(ChannelHandlerContext ctx){
        return ctx.channel().attr(IM_CONTEXT_KEY).get();
    }
    public void setHandlers(ConcurrentHashMap<String, CmdHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        ImContext imContext = getImContext(ctx);
       if(imContext != null){
           imContext.disConnect();
       }
       if(ctx.channel()!=null && ctx.channel().isOpen()){
           ctx.channel().close();
       }
    }

    public void setProvider(BusinessInfoProvider provider) {
        this.infoProvider = provider;
        if(this.sender != null){
            this.sender.setInfoProvider(provider);
        }
    }
    public void setSender(MsgSender sender) {
        this.sender = sender;
        this.sender.setInfoProvider(infoProvider);
    }
}
