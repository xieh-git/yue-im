package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.ImContext;
import com.yueya.im.common.util.JsonMapper;
import com.yueya.im.node.messages.GroupTextMsg;
import com.yueya.im.node.messages.MsgType;
import org.springframework.stereotype.Component;

import static com.yueya.im.node.util.StringUtil.byteToString;

@Component
public class GroupMsgHandler extends DefaultHandler {
    @Override
    public void onMessage(ImContext ctx, int code, byte[] message, long msgId) {
        MsgType msgType = MsgType.forNumber(code);
        String text = byteToString(message);
        if(msgType == MsgType.GROUP_TEXT){
            GroupTextMsg groupTextMessage = JsonMapper.getInstance().fromJson(text,GroupTextMsg.class);
            groupTextMessage.setMsgId(msgId);
            groupTextMessage.setMsgType(code);
            groupTextMessage.setDate(System.currentTimeMillis());
            String msg = JsonMapper.toJsonString(groupTextMessage);
            ctx.sendGroupMessage(msgId,msg,code,groupTextMessage.getGroupId());
            ctx.sendToClient(MsgType.GROUP_TEXT_RESP.getNumber(),"");
        }else if(msgType == MsgType.CREATE_GROUP){
        }else if(msgType == MsgType.EXIT_GROUP){
            //todo 退出群组
        }else{
            ctx.sendToClient(MsgType.UNKNOWN_CODE.getNumber(),"");
        }
    }

    @Override
    public void postMessage(ImContext ctx,int msgType, byte[] message,long msgId) {
    }
}
