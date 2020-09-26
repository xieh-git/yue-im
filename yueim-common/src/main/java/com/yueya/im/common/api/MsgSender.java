package com.yueya.im.common.api;

import com.yueya.im.common.model.ImSession;

public interface MsgSender {

    void sendPointMsg(long msgId, ImSession session, String message, int msgType, String to);

    void sendGroupMessage(long msgId, ImSession session, String message, int msgType, String groupId);

    void disConnect(ImSession session);
    void connect(ImSession session,ImContext ctx);
    void setInfoProvider(BusinessInfoProvider provider);
    void writeMsg(ImContext context, int msgType, String msg);
}
