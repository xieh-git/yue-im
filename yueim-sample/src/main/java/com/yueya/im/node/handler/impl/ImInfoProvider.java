package com.yueya.im.node.handler.impl;

import com.yueya.im.common.api.BusinessInfoProvider;
import com.yueya.im.node.util.UserUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImInfoProvider implements BusinessInfoProvider {

    @Override
    public List<String> mermber(String groupId) {
       return UserUtil.mermber(groupId);
    }

    @Override
    public List<String> friends(String userId) {
        return UserUtil.friends(userId).stream().map(r->r.getFriendId()).collect(Collectors.toList());
    }
}
