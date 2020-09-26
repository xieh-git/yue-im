package com.yueya.im.common.model;

import java.io.Serializable;

public class ImSession implements Serializable {
    private static final long serialVersionUID = 4704866203981566563L;
    private String userId;
    private String sessionId;
    private String host;
    private String device;

    public ImSession(String userId,String sessionId,String device){
        this.userId = userId;
        this.sessionId = sessionId;
        this.device = device;
    }
    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getHost() {
        return host;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ImSession{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", host='" + host + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
