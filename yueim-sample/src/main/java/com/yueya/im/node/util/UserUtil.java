package com.yueya.im.node.util;

import com.yueya.im.node.dtos.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserUtil {
    private static String[] names=new String[]{"白衣秀士","晨日东轩","东方猪猪","莲舟绿影","朝花夕拾","塞鸿秋影","一叶扁舟","春花秋月","晓风残月","红尘往事","夕颜若雪","西风满院","流水落花","月满西楼","疏星淡月","鸿雁天长","人生如梦","野水孤舟","紫霞仙子","帘卷西风","梦寻千古","一枕秋风","日落烟长","秋叶半黄","笑傲江湖","碧水东流","紫袖红弦","青路红尘","夕月幽窗","白云红叶","终南山人","上善如水","往事如风","天涯浪人","湘水北流","闲花落地","上官青云","上官飞燕","天心阁主","云淡风轻","白鹭青天","海角天涯","寻寻觅觅","江山北望","天门山人","陌上花开","陌上红尘","人在江湖","平沙落雁","寻花问柳","阳春白雪","似水流年","行云流水","逍遥公子","风平浪静","似是而非","风萍浪迹","浪迹天涯","今是昨非","月白风清","风花雪月","五湖四海","无牵无挂","无源之水","梅暗花幽","疏帘淡月","天涯海角","无风起浪","叶落归根","一日三秋","一叶知秋","一梦千年","正红旗下","蓝色幽灵","云亭山人","魔教神主","雨夜聆风","一叶秋萍","梅雨青萍","杏花天影","日暮嫣香","一笑嫣然","红梅傲雪","尘封千年","赤炼仙子","毒霸江湖"};
    private static List<Friend> friends = new ArrayList<>();
    static {
        for (int i = 0; i < names.length; i++) {
            Friend friend = new Friend();
            friend.setFriendId("0000"+i);
            friend.setRemarkName(names[i]);
            friends.add(friend);
        }
    }
    public static Friend getUser(){
        Random random = new Random();
        int num = random.nextInt(4);
        return friends.get(num);
    }
    public static List<String> mermber(String groupId) {
        return friends.stream().map(r->r.getFriendId()).collect(Collectors.toList());
    }

    public static List<Friend> friends(String userId){
        return friends.stream()
                .filter(r->!r.getFriendId().equals(userId))
                .limit(5)
                .collect(Collectors.toList());
    }

}
