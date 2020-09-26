package com.yueya.im.server.base;

import akka.actor.ActorRef;
import com.yueya.im.common.api.BusinessInfoProvider;
import com.yueya.im.common.api.ImContext;
import com.yueya.im.common.api.MsgSender;
import com.yueya.im.common.model.ActorMsg;
import com.yueya.im.common.model.ImSession;
import com.yueya.im.server.actors.ImActorSystem;
import com.yueya.im.server.dto.Connector;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.yueya.im.common.constant.ImInfo.SPLITE_CHAR;

public class DefaultSender implements MsgSender {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected BusinessInfoProvider InfoProvider;

    public void connect(ImSession session, ImContext ctx) {
        ActorRef actor = ImActorSystem.getInstance().createUserActor(session);
        ImActorSystem.getInstance().addChannelGroup(session,new Connector(ctx,actor));
        List<String> list = getInfoProvider().friends(session.getUserId());
        list.forEach( to->{
            ActorMsg.ImMessage message = ActorMsg.ImMessage.newBuilder()
                    .setFrom(session.getUserId())
                    .setDevice(session.getDevice())
                    .setTo(to)
                    .build();
            broadCast(message, ActorMsg.CmdType.UP);
        });
    }

    public void sendPointMsg(long msgId, ImSession session, String message, int msgType, String to) {
        ActorRef actor = ImActorSystem.getInstance().getUserActor(session);
        if (actor != null) {
           ActorMsg.ActorCmd cmd = createSendCmd(msgId, session,message,msgType,to);
            actor.tell(cmd,ActorRef.noSender());
        }
    }
    public void sendGroupMessage(long msgId, ImSession session, String message, int msgType, String groupId) {
        ActorRef actor = ImActorSystem.getInstance().getUserActor(session);
        String from = session.getUserId();
        if (actor != null) {
            List<String> mers = getInfoProvider().mermber(groupId);
            mers.stream().filter(mer->!mer.equals(from)).forEach(mer->{
                ActorMsg.ActorCmd cmd = createSendCmd(msgId,session,message,msgType,mer);
                actor.tell(cmd,ActorRef.noSender());
            });
        }
    }
    private ActorMsg.ActorCmd createSendCmd(long msgId, ImSession session, String message, int msgType, String to){
        ActorMsg.ImMessage msg = ActorMsg.ImMessage.newBuilder()
                .setFrom(session.getUserId())
                .setDevice(session.getDevice())
                .setContent(message)
                .setMsgId(msgId)
                .setTo(to)
                .build();
        ActorMsg.ActorCmd cmd = ActorMsg.ActorCmd.newBuilder()
                .setCmdType(ActorMsg.CmdType.SEND)
                .setMsgType(msgType)
                .setMessage(msg).build();
        return cmd;
    }
    @Override
    public void disConnect(ImSession session) {
        ActorRef actor = ImActorSystem.getInstance().getUserActor(session);
        if (actor != null) {
            //通知好友所在节点，客户端下线
            List<String> friends = getInfoProvider().friends(session.getUserId());
            friends.stream().forEach(mer->{
                ActorMsg.ImMessage message = ActorMsg.ImMessage.newBuilder()
                        .setFrom(session.getUserId())
                        .setDevice(session.getDevice())
                        .setTo(mer)
                        .build();
                broadCast(message, ActorMsg.CmdType.DOWN);
            });
            ImActorSystem.getInstance().removeSession(session);
        }
    }


    public BusinessInfoProvider getInfoProvider() {
        return InfoProvider;
    }

    public void setInfoProvider(BusinessInfoProvider infoProvider) {
        this.InfoProvider = infoProvider;
    }


    public void broadCast(ActorMsg.ImMessage msg,ActorMsg.CmdType cmdType){
        ActorMsg.ActorCmd cmd = ActorMsg.ActorCmd.newBuilder()
                .setCmdType(cmdType)
                .setMessage(msg)
                .build();
        ImActorSystem.getInstance().sendRoute.tell(cmd,ActorRef.noSender());
    }

    @Override
    public void writeMsg(ImContext context, int msgType, String content) {
        context.getChannelHandlerContext()
                .channel()
                .writeAndFlush(new TextWebSocketFrame(msgType+SPLITE_CHAR+content));
    }
}
