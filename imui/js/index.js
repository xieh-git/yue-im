var index = (function () {
    function init() {
        $("#add").click(function () {
            var $model = $('#my-prompt');
            $model.modal({
                relatedElement: this,
                onConfirm: function(data) {
                    $.get(api_addr+'/a/api/userlist?loginName='+data.data,function (result) {
                        if(result.data !=null  && result.data.length > 0){
                            chat.addFriend(result.data[0].id);
                        }else{
                            $model.modal('close');
                            $("#alert-content").text("用户不存在");
                            $("#my-alert").modal();
                        }
                    });
                },
                onCancel: function() {
                }
            });
        });
        $("#create_group").click(function () {
            var $model = $("#my-popup");
            $.get(im_bus_addr+api.friend,function (result) {
                if(result.data !=null  && result.data.length > 0){
                    var html = "";
                    for (var i = 0; i < result.data.length; i++) {
                        var item = result.data[i];
                        html += '<li>\n' + '<label class="am-checkbox">\n'
                            +item.remarkName+
                            '<input name="fids" type="checkbox" value="'+item.friendId +'" data-am-ucheck>\n' +
                            '</label></li>\n';
                    }
                    $('#group_friend').html(html);
                    $model.modal({
                        relatedElement: this,
                        height:300,
                        onConfirm: function () {
                            var ids = [];
                            $('#group_friend input:checkbox').each(function() {
                                if ($(this).is(":checked")) {
                                    ids.push($(this).val());
                                }
                            });
                            var gname = $('#group_name').val();
                            if(gname==null || gname=='' || gname==undefined){
                                $model.modal('close');
                                $("#alert-content").text("群组名不能为空");
                                $("#my-alert").modal();
                            }else if(ids.length==0){
                                $model.modal('close');
                                $("#alert-content").text("群组为空，无法建立群组");
                                $("#my-alert").modal();
                            }else{
                                chat.createGroup({
                                    mers:ids,
                                    name:gname
                                });
                            }
                        }
                    })
                }else{
                    $("#alert-content").text("用户不存在");
                    $("#my-alert").modal();
                }
            });
        });
        var faceBox;
        $("body").on('click','a.face-item',function (e) {
            var content = "face["+$(this).attr("title")+"]";
            var text = document.getElementById('input_box');
            text.value = text.value+content;
            layer.close(faceBox);
        });
        $("#face").click(function () {
            var $face = $("#face").offset();
            var top = $face.top-200;
            var left = $face.left-28;
            faceBox = layer.open({
                type:1,
                title: false,
                closeBtn: 0,
                shade: 0.00001,
                shadeClose:true,
                offset:[top,left],
                content: $("#face-list").html(),
                area: ['400px', '190px']
            })
        });
    }
    var pad = function pad(num, n) {
        var len = num.toString().length;
        while(len < n) {
            num = "0" + num;
            len++;
        }
        return num;
    };

    return {
        init:init,
    };
})();