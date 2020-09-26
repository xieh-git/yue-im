package com.yueya.im.node.messages;

import com.yueya.im.common.model.ImMessage;

import java.io.Serializable;

public class FriendMsg extends ImMessage implements Serializable {
    private String fromName;
    private String toName;

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
