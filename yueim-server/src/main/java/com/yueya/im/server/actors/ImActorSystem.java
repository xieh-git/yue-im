package com.yueya.im.server.actors;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.routing.SmallestMailboxPool;
import com.typesafe.config.Config;
import com.yueya.im.common.api.NodeStatusListener;
import com.yueya.im.common.model.ImSession;
import com.yueya.im.common.model.RouteNode;
import com.yueya.im.common.util.ServerNodeManager;
import com.yueya.im.server.config.ConfigConnector;
import com.yueya.im.server.dto.Connector;
import com.yueya.im.server.listener.DeadLetterListener;
import com.yueya.im.server.listener.ImClusterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.yueya.im.common.constant.ImInfo.*;


public class ImActorSystem {
    private Logger logger = LoggerFactory.getLogger(ImActorSystem.class);
    private ActorSystem actorSystem;
    private static ImActorSystem instance;
    /**
     * 节点和sessionId,channel 客户端连接的关系
     */
   // private  ConcurrentHashMap<String, Connector> channelCacheMap = new ConcurrentHashMap();
    /**
     * 用户id和 sessionid,actor的关联关系，接收消息时用
     */
    private  ConcurrentHashMap<String, Map<String,Connector>> userCacheMap = new ConcurrentHashMap();


    /**
     * 用户路由表，发送消息时用
     */
    public static   ConcurrentHashMap<String, RouteNode> userRouteMap = new ConcurrentHashMap(INIT_SIZE);

    public  ActorRef reciveRoute;
    public  ActorRef sendRoute;
    private  final int POOL_SIZE = 6;
    private Timer taskTimes;
    private String selfAddr;
    private ImActorSystem(){}

    public static ImActorSystem getInstance(){
        if(instance == null){
            instance = new ImActorSystem();
        }
        return instance;
    }
    public ActorSystem getActorSystem(){
        return actorSystem;
    }
    public  void init(Config config, NodeStatusListener listener) {
        actorSystem = ActorSystem.create(SYSTEM_NAME, config);
        actorSystem.actorOf(Props.create(ImClusterListener.class,listener),
                "clusterListener");
        String mode = config.getConfig("akka").getConfig("node").getString("mode");
        sendRoute = actorSystem.actorOf(new SmallestMailboxPool(POOL_SIZE).props(Props.create(SendRoute.class)),SEND_ROUTE);
        reciveRoute = actorSystem.actorOf(new SmallestMailboxPool(POOL_SIZE).props(Props.create(ReceiveRoute.class)),REVIVE_ROUTE);
        actorSystem.eventStream().subscribe(actorSystem.actorOf(Props.create(DeadLetterListener.class)),DeadLetter.class);
        //如果是集群模式，链接zookeeper
        if(MODE_CLUSTER.equals(mode)) {
            ConfigConnector configConnector = new ConfigConnector();
            configConnector.init(config.getConfig("akka"),null);
        }else{
            List<String> nodes = new ArrayList<>();
            nodes.add("akka://YueImClusterSystem");
            ServerNodeManager.refreshServers(nodes);
        }
        //启动检测任务
        startCheckTask();
    }

    public  Connector getConnecter(ImSession session) {
        if (!userCacheMap.containsKey(session.getUserId()) || !userCacheMap.get(session.getUserId()).containsKey(session.getSessionId())) {
            return null;
        }
        return userCacheMap.get(session.getUserId()).get(session.getSessionId());
    }

    public  ActorRef getUserActor(ImSession session) {
        if (!userCacheMap.containsKey(session.getUserId())) {
            return null;
        }
        if(!userCacheMap.get(session.getUserId()).containsKey(session.getSessionId())){
            return null;
        }
        return userCacheMap.get(session.getUserId()).get(session.getSessionId()).getActor();
    }

    public  void addChannelGroup(ImSession session, Connector connector) {
        if(userCacheMap.containsKey(session.getUserId())){
            userCacheMap.get(session.getUserId()).put(session.getSessionId(),connector);
        }else{
            ConcurrentHashMap subMap = new ConcurrentHashMap();
            subMap.put(session.getSessionId(),connector);
            userCacheMap.put(session.getUserId(),subMap);
        }

    }

    public  void removeSession(ImSession session) {
        Connector connector = userCacheMap.get(session.getUserId()).remove(session.getSessionId());
        if (connector != null) {
            //终止actor
            connector.getActor().tell(PoisonPill.getInstance(),ActorRef.noSender());
        }
        if(userCacheMap.get(session.getUserId()).isEmpty()){
            userCacheMap.remove(session.getUserId());
        }
    }

    public  boolean exist(String userId) {
        return userCacheMap.containsKey(userId);
    }

    public  String getRoutePath(String node,String userId) {
        // "akka.tcp://YueImClusterSystem@"+
        return node+userId;
    }

    public  String getLocalUserPath(String userId) {
        return "/user/"+USER_PREFIX+userId+"_*";
    }

    public  ActorRef createUserActor(ImSession session) {
        String name = USER_PREFIX+session.getUserId()+"_"+session.getSessionId();
        ActorRef actor = actorSystem.actorOf(Props.create(UserActor.class,session),name);
        return actor;
    }
    public  void refreshServer(List<String> nodes) {
        ServerNodeManager.refreshServers(nodes);
    }


    public  List<String> getNodes(String id) {
        if(userRouteMap.containsKey(id)){
            return userRouteMap.get(id).getnodes();
        }else{
            return null;
        }
    }
    public List<String> getAllNodes(){
        return ServerNodeManager.getServers();
    }
    public  void updateRoute(String id,String device,String node){
        if(!userRouteMap.containsKey(id)){
            RouteNode routeNode = new RouteNode(id,device,node);
            userRouteMap.put(id,routeNode);
        }else{
            userRouteMap.get(id).update(device,node);
        }
    }
    public  void removeRoute(String id,String device,String node){
        if(userRouteMap.containsKey(id)){
            userRouteMap.get(id).remove(device,node);
            if(userRouteMap.get(id).isEmpty()){
                userRouteMap.remove(id);
            }
        }
    }

    public  ConcurrentHashMap<String, RouteNode> getUserRouteMap() {
        return userRouteMap;
    }

    public String getSelfNode(){
        if(selfAddr == null){
            selfAddr = Cluster.get(actorSystem).selfAddress().toString();
        }
        return selfAddr;
    }

    /**
     * 凌晨2：30启动检测清理任务
     */
    private void startCheckTask(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 02); // 控制时
        calendar.set(Calendar.MINUTE, 30);       // 控制分
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        taskTimes = new Timer();
        taskTimes.schedule(new RouteUserCheckTask(),time,1000*60*60*24);
        logger.info("启动路由检测清理定时任务");
    }

}
