package com.yueya.im.server.actors;

import akka.actor.AbstractActor;
import akka.actor.Address;
import com.yueya.im.common.model.ActorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.yueya.im.common.constant.ImInfo.REVIVE_ROUTE;
import static com.yueya.im.common.constant.ImInfo.ROUTE_PREFIX;

/**
 * 集群节点消息发送器
 */
public class SendRoute extends AbstractActor {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Receive createReceive() {
        
        return receiveBuilder()
                .match(ActorMsg.ActorCmd.class, cmd->{
                    String id = cmd.getMessage().getTo();
                    List<String> list = ImActorSystem.getInstance().getNodes(id) ;
                    ActorMsg.ActorCmd.Builder receiveCmdBuilder = ActorMsg.ActorCmd.newBuilder()
                            .setMsgType(cmd.getMsgType())
                            .setMessage(cmd.getMessage());
                    ActorMsg.ActorCmd receiveCmd;
                    boolean knowWhere = (list!=null && !list.isEmpty());
                    if(cmd.getCmdType() == ActorMsg.CmdType.UP ){
                        if(knowWhere){
                            receiveCmd = receiveCmdBuilder .setCmdType(ActorMsg.CmdType.UP).build();
                        }else{
                            receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.UP_BROADCAST).build();
                        }
                        send(list,receiveCmd);
                    }else if (cmd.getCmdType() == ActorMsg.CmdType.DOWN){
                        if(knowWhere){
                            receiveCmd = receiveCmdBuilder .setCmdType(ActorMsg.CmdType.DOWN).build();
                        }else{
                            receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.DOWN_BROADCAST).build();
                        }
                        send(list,receiveCmd);
                    }else if(cmd.getCmdType() == ActorMsg.CmdType.SEND ){
                        if( knowWhere ){
                            receiveCmd = receiveCmdBuilder .setCmdType(ActorMsg.CmdType.RECEIVE).build();
                        }else{
                            receiveCmd = receiveCmdBuilder.setCmdType(ActorMsg.CmdType.BROADCAST).build();
                        }
                        send(list,receiveCmd);
                    }
                    else if(cmd.getCmdType() == ActorMsg.CmdType.ACK){
                        ActorMsg.ImMessage message = cmd.getMessage();
                        Address address = getSender().path().address();
                        ImActorSystem.getInstance().updateRoute(message.getFrom(),message.getDevice(),address.toString());
                    }
                }).build();
    }

    private void  send(List<String> nodes,ActorMsg.ActorCmd cmd){
        if( nodes!=null && !nodes.isEmpty() ){
            for (int i = 0,len=nodes.size(); i < len; i++) {
                context().actorSelection(ImActorSystem.getInstance().getRoutePath(nodes.get(i), ROUTE_PREFIX+REVIVE_ROUTE)).tell(cmd,getSelf());
            }
        }else{
            //获取集群所有节点
            nodes = ImActorSystem.getInstance().getAllNodes().stream().collect(Collectors.toList());
            for (int i = 0,len=nodes.size(); i < len; i++) {
                context().actorSelection(ImActorSystem.getInstance().getRoutePath(nodes.get(i), ROUTE_PREFIX+REVIVE_ROUTE)).tell(cmd,getSelf());
            }
        }
    }
}
