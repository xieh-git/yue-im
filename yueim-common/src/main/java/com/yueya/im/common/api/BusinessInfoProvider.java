package com.yueya.im.common.api;


import java.util.List;

public interface BusinessInfoProvider {

     abstract List<String> mermber(String groupId);
    List<String> friends(String userId);

}
