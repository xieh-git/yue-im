package com.yueya.im.server.actors;

import com.yueya.im.common.model.RouteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 检测节点缓存，定期清理不活跃的节点路由信息
 */
public class RouteUserCheckTask extends TimerTask {
    private static final int LIMIT_TIMES = 6;
    private Logger logger = LoggerFactory.getLogger(RouteUserCheckTask.class);
    @Override
    public void run() {
        Map<String, RouteNode> map = ImActorSystem.getInstance().getUserRouteMap();
       //清理掉通信次数小于阈值的终端路由
        AtomicInteger index = new AtomicInteger(0);
       map.values()
               .stream()
               .forEach(node->{
                   if(node.getTimes()<LIMIT_TIMES || node.isEmpty()){
                       map.remove(node.getUserId());
                       index.incrementAndGet();
                   }else{
                       //重置通信次数
                       node.resetTimes();
                   }
               });
       logger.info("清理不活跃终端路由记录:{}条",index.get());
    }
}
