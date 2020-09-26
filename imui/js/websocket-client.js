var websocketClient = (function(){
    var socket = null;
    var  oneLisener =  {};
    var  onLisener = {};
    var sessionId =null;
    var trigger = function(onLisener, oneLisener, key, args) {
            if (oneLisener[key] !== undefined
                && oneLisener[key].length > 0) {
                for (var i in oneLisener[key]) {
                    oneLisener[key][i].apply(null, [args]);
                    oneLisener[key][i] = undefined;
                }
                oneLisener[key] = [];
            }
            if (onLisener[key] !== undefined
                && onLisener[key].length > 0) {
                for (var i in onLisener[key]) {
                    onLisener[key][i].apply(null, [args]);
                }
            }
    };
    var hearBeatTask = function () {
        setInterval(function () {
            send(0,'');
        },20*1000);
    }
    var connect = function(config) {
            if (!window.WebSocket) {
                window.WebSocket = window.MozWebSocket;
            }
            if (window.WebSocket) {
                socket = new WebSocket(config.server);
                socket.onopen = function (event) {
                    trigger(onLisener, oneLisener, 'connect', event);
                    hearBeatTask();
                };
                socket.onmessage = function (event) {
                    var msgs = event.data.split("#");
                    var data = {};
                    if(msgs.length>1 && msgs[1].length>0){
                        data = JSON.parse(msgs[1]);
                    }
                    data.msgType = parseInt(msgs[0]);
                    if(oneLisener[data.msgType]!=undefined || onLisener[data.msgType]!=undefined){
                       trigger(onLisener, oneLisener, data.msgType, data);
                    }else{
                        trigger(onLisener, oneLisener, 'message', data);
                    }
                };
                socket.onclose = function (event) {
                    trigger(onLisener, oneLisener, 'disconnect', event);
                };
            } else {
                console.log('浏览器不支持websocket');
            }
        };
       var on = function(key, fun) {
            if (onLisener[key] === undefined) {
                onLisener[key] = [];
            }
            onLisener[key].push(fun);
        };
        var one = function(key, fn) {
            if (oneLisener[key] === undefined) {
                oneLisener[key] = [];
            }
            oneLisener[key].push(fn);
        };
        var send = function(msgType, message,fn,respType) {
            console.log(message);
            if (socket.readyState === WebSocket.OPEN) {
                if(respType!= undefined) {
                    one(respType, fn);
                }
                var data = msgType +'#'+ JSON.stringify(message);
                socket.send(data);
            }
        };
        var init = function(config){
            connect(config);
            return websocketClient;
        }
        var setCurrSession = function(sessionID) {
            sessionId = sessionID
        }
        var getTeamMembers = function(id, callback){

        }
        var filterClientAntispam = function(data){
            return {type:0};
        }
    return {
        init:init,
        trigger: trigger,
        on: on,
        one: one,
        send: send,
        connect: connect,
        setCurrSession:setCurrSession,
        getTeamMembers:getTeamMembers,
        filterClientAntispam:filterClientAntispam,
    };
})();
