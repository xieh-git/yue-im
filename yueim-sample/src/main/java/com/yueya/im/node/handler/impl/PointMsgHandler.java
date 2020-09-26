package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.ImContext;
import com.yueya.im.common.util.JsonMapper;
import com.yueya.im.node.messages.MsgType;
import com.yueya.im.node.messages.TextMsg;
import org.springframework.stereotype.Component;

import static com.yueya.im.node.util.StringUtil.byteToString;

@Component
public class PointMsgHandler extends DefaultHandler {


    @Override
    public void onMessage(ImContext ctx, int code, byte[] message, long msgId) {
        MsgType msgType = MsgType.forNumber(code);
        String text = byteToString(message);
        TextMsg textMessage  = JsonMapper.getInstance().fromJson(text, TextMsg.class);
        textMessage.setMsgId(msgId);
        textMessage.setDate(System.currentTimeMillis());
        textMessage.setMsgType(code);
        String msg = JsonMapper.toJsonString(textMessage);
        // 文本信息
        if (msgType == MsgType.POINT_TEXT) {
            ctx.sendPointMsg(msgId,msg,code,textMessage.getTo());
            ctx.sendToClient(MsgType.POINT_TEXT_RESP.getNumber(),"");
        } else if (msgType == MsgType.INVITE_JOIN_GROUP) { //邀请加入群组
            ctx.sendPointMsg(msgId,msg,code,textMessage.getTo());
            ctx.sendToClient(MsgType.INVITE_JOIN_GROUP_RESP.getNumber(),"");
        } else if (msgType == MsgType.DISAGREE_GROUP) { // 拒绝加入群组
            ctx.sendPointMsg(msgId,msg,code,textMessage.getTo());
            ctx.sendToClient(MsgType.DISAGREE_GROUP_RESP.getNumber(),"");
        } else if (msgType == MsgType.ALLOW_GROUP) { //允许加入群组
            ctx.sendPointMsg(msgId,msg,code,textMessage.getTo());
            ctx.sendToClient(MsgType.ALLOW_GROUP_RESP.getNumber(),"");
        }
        else {
            ctx.sendToClient(MsgType.UNKNOWN_CODE.getNumber(),"");
        }
    }

    @Override
    public void postMessage(ImContext ctx, int msgType, byte[] message, long msgId) {
    }
}
