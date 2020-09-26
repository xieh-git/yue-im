package com.yueya.im.server.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import com.yueya.im.common.model.ActorMsg;

import java.util.List;

import static com.yueya.im.common.constant.ImInfo.REVIVE_ROUTE;
import static com.yueya.im.common.constant.ImInfo.ROUTE_PREFIX;

/**
 * 节点消息接收器
 */
public class ReceiveRoute extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorMsg.ActorCmd.class,cmd-> {
                    String id = cmd.getMessage().getTo();
                    String from = cmd.getMessage().getFrom();
                    ActorMsg.ImMessage message = cmd.getMessage();
                    Address address = getSender().path().address();
                    long msgId = cmd.getMessage().getMsgId();
                    //System.out.println("recive-"+from+",cmd:"+cmd.getCmdType()+",to:"+id+",msgType:"+cmd.getMsgType()+",msg:"+message.getContent());
                    switch (cmd.getCmdType()){
                        case RECEIVE:
                        case UP:
                        case DOWN:
                            if(ImActorSystem.getInstance().exist(id)){
                                ActorSelection userActor = getContext().actorSelection(ImActorSystem.getInstance().getLocalUserPath(id));
                                if(userActor != null){
                                    userActor.tell(cmd,getSender());
                                }
                                if(cmd.getCmdType() == ActorMsg.CmdType.DOWN){
                                    removeRoute(cmd);
                                }
                                if(cmd.getCmdType() == ActorMsg.CmdType.RECEIVE || cmd.getCmdType() == ActorMsg.CmdType.UP){
                                    ImActorSystem.getInstance().updateRoute(message.getFrom(),message.getDevice(),address.toString());
                                }
                            }else{
                                NO_ACK(from,id,msgId);
                                //帮助发送节点 广播消息
                                broadCast(cmd,address.toString(),getSender());
                            }
                            break;
                        case BROADCAST:
                        case UP_BROADCAST:
                        case DOWN_BROADCAST:
                            if(ImActorSystem.getInstance().exist(id)){
                                ActorSelection userActor = getContext().actorSelection(ImActorSystem.getInstance().getLocalUserPath(id));
                                if(userActor != null){
                                    userActor.tell(cmd,getSender());
                                }
                                if(cmd.getCmdType() == ActorMsg.CmdType.DOWN_BROADCAST){
                                    removeRoute(cmd);
                                }
                                if(cmd.getCmdType() == ActorMsg.CmdType.BROADCAST || cmd.getCmdType() == ActorMsg.CmdType.UP_BROADCAST){
                                    ImActorSystem.getInstance().updateRoute(message.getFrom(),message.getDevice(),address.toString());
                                }
                            }
                            break;
                        case NO_ACK:
                            removeRoute(cmd);
                            break;
                        case ACK:
                            ImActorSystem.getInstance().updateRoute(message.getFrom(),message.getDevice(),address.toString());
                            break;
                        default:break;

                    }
                })
                .build();
    }
    private void removeRoute(ActorMsg.ActorCmd cmd){
        ActorMsg.ImMessage message = cmd.getMessage();
        Address address = getSender().path().address();
        ImActorSystem.getInstance().removeRoute(message.getFrom(),message.getDevice(),address.toString());
    }

    private void NO_ACK(String from, String to, long msgId){
        ActorMsg.ImMessage message = ActorMsg.ImMessage.newBuilder()
                .setFrom(to)
                .setTo(from)
                .setMsgId(msgId)
                .build();
        ActorMsg.ActorCmd controlCmd = ActorMsg.ActorCmd.newBuilder()
                .setCmdType(ActorMsg.CmdType.NO_ACK)
                .setMessage(message)
                .build();
        getSender().tell(controlCmd,getSelf());
    }

    private void broadCast(ActorMsg.ActorCmd cmd, String sendNode, ActorRef sender){
        List<String> nodes = ImActorSystem.getInstance().getAllNodes();
        ActorMsg.ActorCmd.Builder receiveCmdBuilder = ActorMsg.ActorCmd.newBuilder()
                .setMsgType(cmd.getMsgType())
                .setMessage(cmd.getMessage());
        ActorMsg.ActorCmd receiveCmd = null;
        switch (cmd.getCmdType()){
            case UP: receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.UP_BROADCAST).build();break;
            case DOWN: receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.DOWN_BROADCAST).build();break;
            case RECEIVE: receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.BROADCAST).build();break;
            default:break;
        }
        if(receiveCmd!=null){
            for (int i = 0,len = nodes.size(); i < len; i++) {
                String node = nodes.get(i);
                context()
                        .actorSelection(ImActorSystem.getInstance().getRoutePath(node, ROUTE_PREFIX+REVIVE_ROUTE))
                        .tell(receiveCmd,sender);
            }
        }
    }
}
