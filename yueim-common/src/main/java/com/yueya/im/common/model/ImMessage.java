package com.yueya.im.common.model;


import java.io.Serializable;

/**
 * @Author yueya
 * @Description
 * @Date 2019/4/7
 **/
public class ImMessage implements Serializable {
    private static final long serialVersionUID = 5268701884092008099L;
    protected long msgId;
    protected String from;
    protected String to;
    protected String device;
    protected long date;
    protected int msgType;
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
