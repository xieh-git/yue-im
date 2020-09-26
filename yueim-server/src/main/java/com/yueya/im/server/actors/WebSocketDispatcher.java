package com.yueya.im.server.actors;

import com.yueya.im.common.api.Dispatcher;
import com.yueya.im.server.base.DefaultSender;

public class WebSocketDispatcher extends Dispatcher {
    public WebSocketDispatcher(){
        super();
        this.sender = new DefaultSender();
    }
}
