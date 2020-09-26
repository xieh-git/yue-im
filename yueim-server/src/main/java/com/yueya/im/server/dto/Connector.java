package com.yueya.im.server.dto;

import akka.actor.ActorRef;
import com.yueya.im.common.api.ImContext;

/**
 * @Author yueya
 * @Description
 * @Date 2019/4/8
 **/
public class Connector {
    private ActorRef actor;
    private ImContext ctx;

    public Connector(ImContext ctx, ActorRef actor) {
        this.actor = actor;
        this.ctx = ctx;
    }


    public ActorRef getActor() {
        return actor;
    }

    public void setActor(ActorRef actor) {
        this.actor = actor;
    }

    public ImContext getCtx() {
        return ctx;
    }

    public void setCtx(ImContext ctx) {
        this.ctx = ctx;
    }
}
