package com.yueya.im.node.messages;

import com.yueya.im.common.model.ImMessage;

import java.io.Serializable;

/**
 *  点对点聊天 文本信息
 */
public class TextMsg extends ImMessage implements Serializable {
    private static final long serialVersionUID = 8601728288757610012L;

    private String nickName;
    private String avatar;
    private String content;
    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}