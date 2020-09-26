package com.yueya.im.server.actors;

import akka.actor.AbstractActor;
import com.yueya.im.common.model.ActorMsg;
import com.yueya.im.common.model.ActorMsg.ActorCmd;
import com.yueya.im.common.model.ActorMsg.CmdType;
import com.yueya.im.common.model.ImSession;
import com.yueya.im.server.dto.Connector;


public class UserActor extends AbstractActor {

    private ImSession session;
    public UserActor(ImSession session) {
        this.session = session;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorCmd.class, cmd -> {
                    // 发送信息 用命令做区分，防止出现自己给自己发信息，陷入死循环
                    if(cmd.getCmdType()==CmdType.SEND && session.getUserId().equals(cmd.getMessage().getFrom())){
                        ImActorSystem.getInstance().sendRoute.tell(cmd,getSelf());
                    }else{
                        // 写信息
                        if(cmd.getCmdType() == CmdType.RECEIVE || cmd.getCmdType() == CmdType.BROADCAST ){
                            writeMsg(cmd);
                        }
                        updateRoute(cmd);
                    }
                }).build();
    }
    private void writeMsg(ActorMsg.ActorCmd cmd) {
        Connector connecter = ImActorSystem.getInstance().getConnecter(session);
        if (connecter!=null) {
            connecter.getCtx().sendToClient(cmd.getMsgType(),cmd.getMessage().getContent());
        }
    }

    private void updateRoute(ActorMsg.ActorCmd cmd){
        ActorMsg.ImMessage message = cmd.getMessage();
        switch (cmd.getCmdType()){
            case UP_BROADCAST:
            case BROADCAST:
                ACK(message);
                break;
            default:break;
        }
    }

    private void ACK(ActorMsg.ImMessage msg){
        ActorMsg.ImMessage message = ActorMsg.ImMessage.newBuilder()
                .setFrom(session.getUserId())
                .setTo(msg.getFrom())
                .setDevice(session.getDevice())
                .setMsgId(msg.getMsgId())
                .build();
        ActorMsg.ActorCmd controlCmd = ActorMsg.ActorCmd.newBuilder()
                .setCmdType(ActorMsg.CmdType.ACK)
                .setMessage(message)
                .build();
        getSender().tell(controlCmd,getSelf());
    }

}
