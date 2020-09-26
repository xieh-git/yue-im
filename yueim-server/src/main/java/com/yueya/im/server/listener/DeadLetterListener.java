package com.yueya.im.server.listener;

import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeadLetterListener extends AbstractActor {
    private Logger logger = LoggerFactory.getLogger(DeadLetter.class);
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeadLetter.class, msg->{
                    logger.warn("死信(没有actor接收):from{},to{}",msg.sender().path().toString(),msg.recipient().path().toString());
        }).build();
    }
}
