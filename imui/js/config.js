//var api_addr = 'http://47.102.218.172:8099/rest/';
var api_addr = 'http://127.0.0.1:8080/';
var im_bus_addr = 'http://127.0.0.1:8089';
var im_server_addr = 'ws://127.0.0.1:8088/websocket';
var api ={
    friend: '/a/friend/list',
    group: '/a/group/list'
}
var token = sessionStorage.getItem("accessToken");
if(token!=null){
    $.ajaxSetup({
        headers:{
            "auth":token
        }
    });
    $(document).ajaxError(function (event,jqxhr,setting) {
        if(jqxhr.status == 405) {
            $.ajax({
                url:api_addr+'token',
                type:'post',
                contentType:'application/json;charset=UTF-8',
                data:JSON.stringify({token:sessionStorage.getItem('refreshToken')}),
                success:function (result) {
                    if(result.code == 200) {
                        sessionStorage.setItem('accessToken',result.data);
                        setting.headers = {auth:sessionStorage.getItem("accessToken")};
                        $.ajax(setting);
                        token = sessionStorage.getItem("accessToken");
                    }else{
                        location='login.html';
                    }
            },
            })
        }
    })
}