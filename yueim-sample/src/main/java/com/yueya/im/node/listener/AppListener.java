package com.yueya.im.node.listener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yueya.im.common.api.BusinessInfoProvider;
import com.yueya.im.common.api.CmdHandler;
import com.yueya.im.common.api.ImSetting;
import com.yueya.im.common.api.NodeStatusListener;
import com.yueya.im.server.ClusterNodeApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.ConcurrentHashMap;

public class AppListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Config config = ConfigFactory.load("handler.conf").getConfig("handler");
        ConcurrentHashMap<String, CmdHandler> handlerMap = new ConcurrentHashMap<>(20);
        ApplicationContext context = event.getApplicationContext();
            config.entrySet().stream().forEach(r -> {
                try {
                    Class handlerClass =Class.forName(config.getString(r.getKey()));
                    handlerMap.put(r.getKey(),(CmdHandler)context.getBean(handlerClass));
                } catch (ClassNotFoundException e) {
                   logger.error("初始化业务处理类异常:",e);
                }
            });
        BusinessInfoProvider provider = context.getBean(BusinessInfoProvider.class);
        NodeStatusListener listener = context.getBean(NodeStatusListener.class);
        ImSetting setting = new ImSetting();
        setting.setBusinessInfoProvider(provider);
        setting.setHandlerMap(handlerMap);
        setting.setListener(listener);
        try {
            ClusterNodeApp.start("akka.conf",setting);
        } catch (Exception e) {
            logger.error("服务启动异常",e);
        }
    }
}
