package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.ImContext;
import com.yueya.im.common.util.JsonMapper;
import com.yueya.im.node.messages.FriendMsg;
import com.yueya.im.node.messages.MsgType;
import org.springframework.stereotype.Component;

import static com.yueya.im.node.util.StringUtil.byteToString;

@Component
public class FriendHandler extends DefaultHandler  {

    @Override
    public void onMessage(ImContext ctx, int code, byte[] message, long msgId) {
        String text = byteToString(message);
        MsgType msgType = MsgType.forNumber(code);
        FriendMsg friendMsg = JsonMapper.getInstance().fromJson(text, FriendMsg.class);
        friendMsg.setMsgId(msgId);
        friendMsg.setMsgType(code);
        friendMsg.setDate(System.currentTimeMillis());
       String msg = JsonMapper.toJsonString(friendMsg);
        if (msgType == MsgType.ADD_FRIEND) { // 好友请求
            ctx.sendPointMsg(msgId,msg,code,friendMsg.getTo());
            ctx.sendToClient(MsgType.ADD_FRIEND_RESP.getNumber(),"");
        } else if (msgType == MsgType.DISAGREE_FRIEND) { //拒绝好友请求
            ctx.sendPointMsg(msgId,msg,code,friendMsg.getTo());
            ctx.sendToClient(MsgType.DISAGREE_FRIEND_RESP.getNumber(),"");
        } else if (msgType == MsgType.ALLOW_FRIEND) { // 同意好友请求
            ctx.sendPointMsg(msgId,msg,code,friendMsg.getTo());
            ctx.sendToClient(MsgType.ALLOW_FRIEND_RESP.getNumber(),"");
        }else {
            ctx.sendToClient(MsgType.UNKNOWN_CODE.getNumber(),"");
        }
    }


    @Override
    public void postMessage(ImContext ctx,int msgType, byte[] message,long msgId) {

    }
}
