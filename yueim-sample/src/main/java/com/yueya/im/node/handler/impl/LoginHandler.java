package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.ImContext;
import com.yueya.im.common.util.JsonMapper;
import com.yueya.im.node.dtos.Friend;
import com.yueya.im.node.messages.LoginMsg;
import com.yueya.im.node.messages.MsgType;
import com.yueya.im.node.util.UserUtil;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginHandler extends DefaultHandler {

    @Override
    public void onMessage(ImContext ctx, int msgType, byte[] message, long msgId) {
        if(ctx.getSessionId() != null) {
            return;
        }
        try {
            String msg  = new String(message,"utf-8");
            LoginMsg loginDto = JsonMapper.getInstance().fromJson(msg, LoginMsg.class);
            String device = loginDto.device;
            Map<String,Object> userInfo = new HashMap<>();
            Friend user = UserUtil.getUser();
            userInfo.put("userId",user.getFriendId());
            userInfo.put("name",user.getRemarkName());
            userInfo.put("loginName",user.getRemarkName());
            List<Friend> friends = UserUtil.friends(user.getFriendId());
            userInfo.put("friends",friends);
            ctx.connect(user.getFriendId(),device);
            ctx.sendToClient(MsgType.LOGIN_RESP.getNumber(),JsonMapper.toJsonString(userInfo));
        } catch (UnsupportedEncodingException e) {
            logger.error("编码异常:",e);
        }
    }

}

