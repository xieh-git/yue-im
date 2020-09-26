package com.yueya.im.node.listener;

import com.yueya.im.common.api.NodeStatusListener;
import org.springframework.stereotype.Component;

@Component
public class DefaultNodeListener implements NodeStatusListener {
    @Override
    public void onMerUp(String nodeIp) {

    }

    @Override
    public void onMerDown(String nodeIp) {

    }
}
