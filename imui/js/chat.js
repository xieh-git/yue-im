var chat = (function(){

    var user = {
    }
    var curSub={

    }
    var msgCache={};
    var params={
        sub:1,
        width:'40px',
        height:'40px',
        fontSize:'26px'
    };
    var socket = window.websocketClient;
    var init = function(options){
        socket.on('message',function(msg){
            //文本信息
            if(msg.msgType == 2000 || msg.msgType == 2003){
                updateMessage(msg,msg.msgType);
            }
            if(msg.msgType == 2015) {
                showApply(msg);
            }
        });
        socket.connect({
            server:im_server_addr,
        })
        socket.on('connect',function(msg){
            socket.send('1000',{
                'device':uuid()
            },function(resp){
                user.id = resp.userId;
                user.nickName = resp.name;
                setUser();
                user.friends = resp.friends;
                var fmap = {};
                for (var i = 0; i < user.friends.length; i++) {
                    fmap[user.friends[i].friendId] = user.friends[i].remarkName;
                }
                user.fmap = fmap;
                user.groups = [{"groupId":1000000001,"groupName":"公共测试群组","remark":null,"adminId":2,"createGmt":null,"status":null,"groupIcon":null}];
                updateFriends();
            },3000)
        });
     $(".user_list").on('click','li',function(){
         $(this).addClass();
         var id = $(this).data("id");
         var type = $(this).data("type");
         curSub = getSubject(id,type);
        if(type == 'friend'){
            $("#chat_subject").text(curSub.remarkName);
        }else{
            $("#chat_subject").text(curSub.groupName);
        }
         $(".bardge").hide();
         $('#chatbox').html('');
         var item = msgCache[type+"_"+id];
         if(item != undefined){
             item.forEach(function(msg){
                 setMsg(msg);
             })
         }
     });

     $(".friends_list").on('click','li',function(){
         var id = $(this).data("id");
         var type = $(this).data("type");
         curSub = getSubject(id,type);
        if(type == 'friend'){
            $("#chat_subject").text(curSub.remarkName);
        }else{
            $("#chat_subject").text(curSub.groupName);
        }
        // 清空消息
        $('#chatbox').html('');
         var item = msgCache[type+"_"+id];
         if(item != undefined){
             item.forEach(function(msg){
                 setMsg(msg);
             })
         }
     });
     setInterval(refreshAuth,1000*60*8);
    }
    var refreshAuth= function () {
        $.ajax({
            url:api_addr+'token',
            type:'post',
            contentType:'application/json;charset=UTF-8',
            data:JSON.stringify({token:sessionStorage.getItem('refreshToken')}),
            success:function (result) {
                if(result.code == 200) {
                    sessionStorage.setItem('accessToken',result.data);
                    token = sessionStorage.getItem("accessToken");
                }else{
                    console.log(result)
                    //location='login.html';
                }
            },
        })
    }
    var addFriend = function (friendId) {
        socket.send('2015',{
            from:user.id,
            to:friendId,
            fromName:user.nickName,
            toName:''
        });
    }
    var showApply = function (msg) {
        $("#friend-messages").text('来自'+msg.fromName+'的好友申请');
        $('#my-confirm').modal({
            relatedElement: this,
            onConfirm: function() {
                agreeFriend(msg.from,msg.fromName);
            },
            onCancel: function() {
                disAgreeFriend(msg.from,msg.fromName);
            }
        });
    }
    var agreeFriend = function (fromId,fromName) {
        socket.send(2017,{
            from:user.id,
            to:fromId,
            toName:fromName,
            fromName:user.nickName
        });
    }
    var disAgreeFriend = function (fromId,fromName) {
        socket.send(2007,{
            from:user.id,
            to:fromId,
            toName:fromName,
            fromName:user.nickName
        })

    }
    var setUser = function(){
        $(".own_name").text(user.nickName);
        $("#own_avatar").html('<div class="own_avatar" data-sex="M" data-name="'+user.nickName+'">');
        $(".own_head").html('<div class="own_avatar" data-sex="M" data-name="'+user.nickName+'">');
        $(".own_avatar").avatarIcon(params);
        $(".own_numb").text("IM账号："+user.loginName);
    }
    var getSubject = function(id,type){
        var result;
        if(type == 'friend'){
            for(var i=0;i < user.friends.length;i++){
                var item = user.friends[i];
                if(item.friendId == id){
                    result =  item;
                    result.type = 'friend'
                    break;
                }
            }
        }
        if(type == 'group'){
            for(var i=0;i < user.groups.length;i++){
                var item = user.groups[i];
                if(item.groupId == id){
                    result =  item;
                    result.type = 'group';
                    break;
                }
            }
        }
        return result;
    }
    var updateFriends = function(){
       var tpl ="<li data-id='{{id}}' data-type='{{type}}'>" +
       "                            <div class=\"friends_box\">\n" +
       "                                <div class=\"user_head\"><div class=\"avatar\" data-sex=\"M\" data-name=\"{{name}}\"></div></div>\n" +
       "                                <div class=\"friends_text\">\n" +
       "                                    <p class=\"user_name\">{{name}}</p>\n" +
       "                                </div>\n" +
       "                            </div>\n" +
       "                        </li>";
       var html = "";
       user.friends.forEach(function(item){
           html = html + Mustache.render(tpl,{
               "id":item.friendId,
               "type":"friend",
               "name":item.remarkName
           })
       })
       user.groups.forEach(function(item){
           html = html + Mustache.render(tpl,{
               "id":item.groupId,
               "type":"group",
               "name":item.groupName,
           })
       })
    $(".friends_list").html(html)
    $('.friends_list .avatar').avatarIcon(params);
    }
    var cacheMsg = function (msg,id) {
        var type = msg.msgType == 2000 ? 'friend' : 'group';
        var item = msgCache[type+"_"+id];
        if(item!=null){
            item.push(msg)
        }else{
            msgCache[type+"_"+id] = [msg];
        }
    }
    var time = function time(time) {
        var date = new Date(time + 8 * 3600 * 1000); // 增加8小时
        return date.toJSON().substr(0, 16).replace('T', ' ');
    }
    var updateMsgList = function(msg){
        var tempList = [];
        for(var item in msgCache){
            var list =msgCache[item];
            var latest = list[list.length-1];
            tempList.push({
                id: latest.msgType == 2000 ? latest.from : latest.groupId,
                type : latest.msgType == 2000 ? 'friend' : 'group',
                name : latest.msgType==2000?user.fmap[latest.from]:latest.groupName,
                content : latest.content,
                date:time(Number(latest.date))
            })
        }
       var tpl ="{{#msgs}}<li class=\"user_active\" data-id='{{id}}' data-type='{{type}}' >\n" +
       "                            <div class=\"avatar\" data-sex=\"M\" data-name=\"{{name}}\"></div>\n" +
       "                            <div class=\"user_text\">\n" +
       "                                <p class=\"user_name\">{{name}}</p>\n" +
       "                                <p class=\"user_message\">{{content}}</p>\n" +
       "                            </div>\n" +
       "                            <div class=\"user_time\">{{date}}</div>\n" +
       "                        </li>{{/msgs}}";
       var html = Mustache.render(tpl,{
           msgs:tempList
       });
    $(".user_list").html(html);
    $('.avatar').avatarIcon(params);
    }

    var updateMessage = function(msg,msgType){
        if(curSub.type == 'friend' && curSub.friendId == msg.from && msgType == 2000){
            setMsg(msg);
            cacheMsg(msg,msg.from);
        }else if(curSub.type == 'group' && curSub.groupId == msg.groupId  && msgType == 2003){
            setMsg(msg);
            cacheMsg(msg,msg.groupId);
        }else{
            $(".bardge").show();
            if(msgType==2000){
                cacheMsg(msg,msg.from);
            }else{
                cacheMsg(msg,msg.groupId);
            }
            updateMsgList(msg);
        }
    }
    var setMsg = function(msg){
        var chat = document.getElementById('chatbox');
        msg.content = content = setFace(msg.content);
        if(msg.from == user.id){
            chat.innerHTML += '<li class="me"><div class=\"me_avatar\" data-sex=\"M\" data-name=\"'+user.nickName+'\"></div><span>' + msg.content + '</span></li>';
            $("#chatbox .me_avatar").last().avatarIcon(params);
        } else{
            chat.innerHTML += '<li class="other"><div class=\"avatar\" data-sex=\"M\" data-name="'+user.fmap[msg.from]+'"></div><span>' + msg.content + '</span></li>';
            $("#chatbox .avatar").last().avatarIcon(params);
        }
        var height = $("#chatbox").height();
        $('.office_text').animate({scrollTop:height}, 400);
    }
    var sendMsg = function() {
        var text = document.getElementById('input_box');
        var chatbox = document.getElementById('chatbox');
        var talk = document.getElementById('talkbox');
        var content = encode(text.value);

        if(curSub.type == 'friend'){
            var msg = {
                'from':user.id,
                'nickName':user.nickName,
                'to':curSub.friendId,
                'content':content
            };
            socket.send('2000',msg);
            msg['msgType']=2000;
            cacheMsg(msg,msg.to);
        }else if(curSub.type == 'group'){
            var msg = {
                'from':user.id,
                'to':curSub.groupId,
                'groupId':curSub.groupId,
                'nickName':user.nickName,
                'groupName':curSub.groupName,
                'content':content
            };
            msg['date']= new Date().getTime();
            socket.send('2003',msg);
            msg['msgType']=2003;
            cacheMsg(msg,msg.to);
        }
        content = setFace(content);
        chatbox.innerHTML += '<li class="me"><div class=\"me_avatar\" data-sex=\"M\" data-name=\"'+user.nickName+'\"></div><span>' + content + '</span></li>';
        text.value = '';
        $("#chatbox .me_avatar").last().avatarIcon(params);
        talk.style.background = "#fff";
        text.style.background = "#fff";
        var height = $("#chatbox").height();
        $('.office_text').animate({scrollTop:height}, 400);
    }
    var encode = function(sHtml) {
        return sHtml.replace(/[<>&"]/g,function(c){
            return {'<':'&lt;','>':'&gt;','&':'&amp;','"':'&quot;'}[c];
        });
    }
    var setFace = function (content) {
        var faces = content.match(/(?<=face\[).*?(?=\])+/g);
        if(faces!=null){
            var $iconList = $("#face-list");
            for (var i = 0; i < faces.length; i++) {
                var name = faces[i];
                var icon = $iconList.find('a[title="'+name+'"]').html();
                content = content.replace('face['+name+']',icon)
            }
        }
        return content;
    }
    var createGroup = function (param) {
        socket.send(2006,{
            memberIds:param.mers,
            groupName:param.name,
            admin:user.id
        },function (result) {
            console.log(result);
        },4006)
    }
    var uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });

    }
    return{
        init:init,
        sendMsg:sendMsg,
        addFriend:addFriend,
        createGroup:createGroup,
        
    }
})();
chat.init()