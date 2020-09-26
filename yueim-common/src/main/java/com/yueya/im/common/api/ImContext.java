package com.yueya.im.common.api;

import com.yueya.im.common.model.ImSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.UUID;

public class ImContext {
    public static final AttributeKey<ImSession> IM_SESSION_KEY = AttributeKey.valueOf("netty.channel.session");
    public static final AttributeKey<ImContext> IM_CONTEXT_KEY = AttributeKey.valueOf("netty.channel.context");
    private ChannelHandlerContext channelHandlerContext;
    private MsgSender sender;
    private int idleNum = 0;
    public ImContext(ChannelHandlerContext context,MsgSender sender){
        this.channelHandlerContext = context;
        this.sender = sender;
    }

    /**
     * 客户端与服务器正式建立连接
     * @param userId 用户id
     * @param device 终端id
     */
    public void connect(String userId,String device) {
        ImSession oldSession = getSession();
        if(oldSession != null){
            return;
        }
        String sessionId = UUID.randomUUID().toString();
        ImSession session = new ImSession(userId,sessionId,device);
        Attribute<ImSession> sessionAttribute = channelHandlerContext.channel().attr(IM_SESSION_KEY);
        sessionAttribute.set(session);
        this.sender.connect(session,this);
    }
    public ImSession getSession(){
        return channelHandlerContext.channel().attr(IM_SESSION_KEY).get();
    }
    public String getSessionId() {
        ImSession session = channelHandlerContext.channel().attr(IM_SESSION_KEY).get();
        if(session!=null){
            return session.getSessionId();
        }
        return null;
    }

    /**
     *  发送单点消息
     * @param msgId  消息id
     * @param msg 消息内容
     * @param msgType 消息类型码
     * @param to  接收者id
     */
    public void sendPointMsg(long msgId,String msg,int msgType,String to){
        sender.sendPointMsg(msgId,getSession(),msg,msgType,to);
    }

    /**
     *  发送群组消息
     * @param msgId 消息id
     * @param msg 消息内容
     * @param msgType 消息类型码
     * @param groupId 群组id
     */
    public void sendGroupMessage(long msgId,String msg,int msgType,String groupId){
        sender.sendGroupMessage(msgId,getSession(),msg,msgType,groupId);
    }

    /**
     * 向终端发送消息
     * @param msgType 消息类型
     * @param msg 消息内容
     */
    public void sendToClient(int msgType,String msg) {
        sender.writeMsg(this,msgType,msg);
    }

    public ChannelHandlerContext getChannelHandlerContext(){
        return this.channelHandlerContext;
    }

    public void disConnect() {
        sender.disConnect(getSession());
    }

    public int addIdleNum() {
        return idleNum++;
    }

    public void resetIdleNum() {
        this.idleNum = 0;
    }
}
