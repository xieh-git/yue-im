package com.yueya.im.server;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.common.api.ImSetting;
import com.yueya.im.server.actors.ImActorSystem;
import com.yueya.im.server.handler.DefaultBeatHander;
import com.yueya.im.server.netty.NettyServer;

import static com.yueya.im.common.constant.ImInfo.LIMIT_CODE_MAX;
import static com.yueya.im.common.constant.YueMsgCode.MSG_HEART_BEAT;

public class ClusterNodeApp {
    private static Config root;
    public static void start(String conf, ImSetting setting){
        root = ConfigFactory.load(conf);
        ImActorSystem.getInstance().init(root,setting.getListener());
        NettyServer server = new NettyServer();
        int port = root.getConfig("akka").getConfig("netty").getInt("port");
        String mode = root.getConfig("akka").getConfig("netty").getString("mode");
        boolean heartbeat = root.getConfig("akka").getConfig("netty").getBoolean("hear_beat");
        setting.setHearBeatConfig(heartbeat);
        checkHandler(setting);
        server.start(setting,port,mode);
    }

    private static void checkHandler(ImSetting setting){
        setting.getHandlerMap().forEach((k,v)->{
            int code = Integer.parseInt(k);
            if(code < LIMIT_CODE_MAX){
                throw new IllegalArgumentException("请勿使用系统保留的消息类型码(0-10):"+code);
            }
        });
      if(setting.getHearBeatConfig()){
          //加入心跳消息处理器
          CmdHandler heartBeatHandler = new DefaultBeatHander();
          setting.getHandlerMap().put(MSG_HEART_BEAT+"",heartBeatHandler);
      }
    }

}
