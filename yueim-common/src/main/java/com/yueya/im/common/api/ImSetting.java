package com.yueya.im.common.api;

import java.util.concurrent.ConcurrentHashMap;

public class ImSetting {
    private BusinessInfoProvider businessInfoProvider;
    private NodeStatusListener listener;
    private ConcurrentHashMap<String,CmdHandler> handlerMap;
    private boolean hearBeatConfig;

    public BusinessInfoProvider getBusinessInfoProvider() {
        return businessInfoProvider;
    }

    public void setBusinessInfoProvider(BusinessInfoProvider businessInfoProvider) {
        this.businessInfoProvider = businessInfoProvider;
    }

    public NodeStatusListener getListener() {
        return listener;
    }

    public void setListener(NodeStatusListener listener) {
        this.listener = listener;
    }

    public ConcurrentHashMap<String, CmdHandler> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(ConcurrentHashMap<String, CmdHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public boolean getHearBeatConfig() {
        return hearBeatConfig;
    }

    public void setHearBeatConfig(boolean hearBeatConfig) {
        this.hearBeatConfig = hearBeatConfig;
    }
}
