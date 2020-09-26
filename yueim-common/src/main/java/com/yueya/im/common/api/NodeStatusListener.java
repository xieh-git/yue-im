package com.yueya.im.common.api;

public interface NodeStatusListener {
    /**
     * 节点上线触发
     */
    public void onMerUp(String nodeIp);

    /**
     * 节点下线触发
     */
    public void onMerDown(String nodeIp);
}
