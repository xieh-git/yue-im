package com.yueya.im.node.messages;

import com.yueya.im.common.model.ImMessage;

import java.io.Serializable;

/**
 * @Author yueya
 * @Description
 * @Date 2019/4/7
 **/
public class GroupTextMsg extends ImMessage implements Serializable {
    private static final long serialVersionUID = -7287149408403694937L;
    private String nickName;
    private String groupName;
    private String groupIcon;
    private String content;
    private String groupId;
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
