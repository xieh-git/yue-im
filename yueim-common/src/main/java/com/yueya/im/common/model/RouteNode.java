package com.yueya.im.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteNode {
    private String userId;
    /**
     * 计数器
     */
    private AtomicInteger times = new AtomicInteger(0);
    /**
     * 连接和节点地址的路由表 node->device
     */
    private ConcurrentHashMap<String, Map<String,String>> nodeMap = new ConcurrentHashMap<>();

    public RouteNode(String userId,String device,String node){
        this.userId = userId;
        Map<String,String> deviceMap = new ConcurrentHashMap<>();
        deviceMap.put(device,"");
        this.nodeMap.put(node,deviceMap);
    }
    public int getTimes() {
        return this.times.get();
    }

    public ConcurrentHashMap<String, Map<String,String>> getNodeMap() {
        this.times.incrementAndGet();
        return nodeMap;
    }

    public void update(String device,String node){
        if(!this.nodeMap.containsKey(node)){
            Map<String,String> deviceMap = new ConcurrentHashMap<>();
            deviceMap.put(device,"");
            this.nodeMap.put(node,deviceMap);
        }else{
            Map<String,String> deviceMap = this.nodeMap.get(node);
            if(!deviceMap.containsKey(device)){
                deviceMap.put(device,"");
            }
        }
        this.times.incrementAndGet();
    }

    public List<String> getnodes() {
        this.times.incrementAndGet();
        return new ArrayList<>(nodeMap.keySet());
    }

    public void remove(String device,String node) {
        this.nodeMap.get(node).remove(device);
        if(this.nodeMap.get(node).isEmpty()){
            this.nodeMap.remove(node);
        }
    }

    public String getUserId() {
        return userId;
    }

    public void resetTimes() {
        this.times.set(0);
    }

    public boolean isEmpty() {
        return this.nodeMap.isEmpty();
    }
}
