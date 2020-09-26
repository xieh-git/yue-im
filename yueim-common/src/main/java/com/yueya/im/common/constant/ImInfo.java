package com.yueya.im.common.constant;

public class ImInfo {
    public static final String USER_PREFIX = "userActor_";
    public static final String ROUTE_PREFIX = "/user/";
    public static final String SEND_ROUTE= "SendRoute";
    public static final String REVIVE_ROUTE= "ReciveRoute";
    public static final String SYSTEM_NAME = "YueImClusterSystem";
    public static final String MODE_SINGLE = "single";
    public static final String MODE_CLUSTER = "cluster";
    public static final String SPLITE_CHAR = "#";
    /**
     * 路由节点默认容量
     */
    public static final int INIT_SIZE = 400000;
    /**
     * 心跳检测间隔 10s
     */
    public static final int HEARTBEAT_IDLE = 20;
    /**
     * 超时次数，超过检测次数则断开连接
     */
    public static final int CHECK_TIMES = 3;
    /**
     * 保留消息代码
     */
    public static final int LIMIT_CODE_MAX = 10;
}
