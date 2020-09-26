package com.yueya.im.common.api;

public interface CmdHandler {

    //public void connect(ImSession session, ChannelHandlerContext ctx);
    /**
     * 服务器收到消息前的回调
     * @param msgType
     * @param message
     * @param msgId
     * @return 如果返回false，后续的回调函数将不会执行
     */
      boolean preMessage(int msgType, byte[] message, long msgId);

    /**
     *  服务器处理消息的回调
     * @param ctx
     * @param msgType
     * @param message
     * @param msgId
     * @return
     */
    void onMessage(ImContext ctx, int msgType, byte[] message, long msgId);

    /**
     * 处理完消息后的回调(响应客户端后触发)
     * @param ctx
     * @param msgType
     * @param message
     * @param msgId
     */
    void postMessage(ImContext ctx, int msgType, byte[] message,long msgId);
}
