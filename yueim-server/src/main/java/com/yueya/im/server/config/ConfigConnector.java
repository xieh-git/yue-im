package com.yueya.im.server.config;

import akka.actor.Address;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.yueya.im.server.actors.ImActorSystem;
import com.yueya.im.server.listener.ConfigListener;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.yueya.im.common.constant.ImInfo.SYSTEM_NAME;

public class ConfigConnector implements Watcher {
    Logger logger = LoggerFactory.getLogger(getClass());
    private ZooKeeper zookeeper;
    private ConfigListener listener;
    private String path = "/nodes";
    private String ip;
    private String port;
    private int seedNodeCount = 3;
    public void init(Config config,ConfigListener listener) {
        this.listener = listener;
        String zookAddress = config.getConfig("zookeeper").getString("ip");
        ip = config.getConfig("remote").getConfig("netty.tcp").getString("hostname");
        port = config.getConfig("remote").getConfig("netty.tcp").getString("port");
        //seednodeCount = config.getConfig("node").getInt("seed_num");
        try {
            zookeeper = new ZooKeeper(zookAddress, 5000, this);
        } catch (IOException e) {
            listener.onError();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            logger.info("zookeeper已连接");
            List<String> nodes = getChildren(path);
            String uri = ip + ":" + port;
            try {
                if (nodes == null || nodes.size() == 0) {
                    if(nodes == null) {
                        createRootNode(path,"");
                    }
                    createNode(path + "/" + uri, uri);
                    joinCluster(Collections.singletonList(new Address("akka.tcp",SYSTEM_NAME,ip,Integer.valueOf(port))));
                } else {
                    createNode(path + "/" + uri, uri);
                    //已有节点启动,获取种子节点
                    int nodeSize = nodes.size() < seedNodeCount ? nodes.size() : seedNodeCount;
                    if(nodeSize > 0){
                        List<Address> list = nodes.stream()
                                .limit(nodeSize)
                                .map(uriItem -> {
                                    String ip = uriItem.substring(0,uriItem.indexOf(":"));
                                    String port = uriItem.substring(uriItem.indexOf(":")+1,uriItem.length());
                                    return new Address("akka.tcp",SYSTEM_NAME,ip,Integer.valueOf(port));
                                })
                                .collect(Collectors.toList());
                        joinCluster(list);
                    }
                }
            } catch (KeeperException |InterruptedException e) {
                logger.error("连接zookeeper异常",e);
            }
        }
    }

    public void joinCluster(List<Address> seeds) {
        Cluster.get(ImActorSystem.getInstance().getActorSystem()).joinSeedNodes(seeds);
    }
    /**
     * 创建节点
     * @param path
     * @param data
     * @throws Exception
     */
    public String createNode(String path,String data) throws KeeperException, InterruptedException {
        return this.zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    public String createRootNode(String path,String data) throws KeeperException, InterruptedException {
        return this.zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    /**
     * 获取路径下所有子节点
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<String> getChildren(String path){
        List<String> children = null;
        try {
            children = zookeeper.getChildren(path, false);
        } catch (KeeperException e) {
            logger.error("error",e);
            return children;
        } catch (InterruptedException e) {
            logger.error("error",e);
        }
        return children;
    }

    /**
     * 获取节点上面的数据
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getData(String path) throws KeeperException, InterruptedException{
        byte[] data = zookeeper.getData(path, false, null);
        if (data == null) {
            return "";
        }
        return new String(data);
    }

    /**
     * 设置节点信息
     * @param path
     * @param data
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Stat setData(String path, String data) throws KeeperException, InterruptedException{
        Stat stat = zookeeper.setData(path, data.getBytes(), -1);
        return stat;
    }

    /**
     * 删除节点
     * @param path
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void deleteNode(String path) throws InterruptedException, KeeperException{
        zookeeper.delete(path, -1);
    }

    /**
     * 获取创建时间
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getCTime(String path) throws KeeperException, InterruptedException{
        Stat stat = zookeeper.exists(path, false);
        return String.valueOf(stat.getCtime());
    }

    /**
     * 获取某个路径下孩子的数量
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Integer getChildrenNum(String path) throws KeeperException, InterruptedException{
        int childenNum = zookeeper.getChildren(path, false).size();
        return childenNum;
    }
    /**
     * 关闭连接
     * @throws InterruptedException
     */
    public void closeConnection() throws InterruptedException{
        if (zookeeper != null) {
            zookeeper.close();
        }
    }
}
