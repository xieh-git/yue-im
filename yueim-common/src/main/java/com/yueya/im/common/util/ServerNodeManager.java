package com.yueya.im.common.util;

import java.util.LinkedList;
import java.util.List;

public class ServerNodeManager {

    /**
     * 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
     */
    private static List<String> realNodes = new LinkedList<>();


    public static void refreshServers(List<String> servers) {
        realNodes.clear();
        realNodes.addAll(servers);
    }

    public static List<String> getServers(){
        return realNodes;
    }


}
