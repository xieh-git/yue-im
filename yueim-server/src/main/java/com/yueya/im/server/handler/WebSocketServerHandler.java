package com.yueya.im.server.handler;

import com.yueya.im.server.actors.WebSocketDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import static com.yueya.im.common.constant.ImInfo.SPLITE_CHAR;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.stomp.StompHeaders.HOST;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketDispatcher webSocketDispatcher;
    private WebSocketServerHandshaker handshaker;
    private static final String WEBSOCKET_PATH = "/websocket";
    private static final String CHARSET = "utf-8";
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx,(FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) throws UnsupportedEncodingException {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        if (frame instanceof  TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            int index = text.indexOf(SPLITE_CHAR);
            if(index > -1){
                byte[] bytes = text.substring(index+1).getBytes(CHARSET);
                int msgType = Integer.valueOf(text.substring(0,index));
                handleTextMsg(msgType,bytes,ctx);
            }
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
      webSocketDispatcher.handlerRemoved(ctx);
    }

    private void handleTextMsg(int code, byte[] content, ChannelHandlerContext ctx) {
        webSocketDispatcher.handle(code,content,ctx);
    }
    private void handleHttpRequest (ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!request.decoderResult().isSuccess()
                || (!"websocket".equals(request.headers().get("Upgrade")))) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            request.headers().set("Access-Control-Allow-Origin","*");
            request.headers().set("Access-Control-Allow-Origin","*");
            request.headers().set("Access-Control-Allow-Methods","GET, POST, OPTIONS");
            request.headers().set("Access-Control-Allow-Headers","DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization");
            sendHttpResponse(ctx,request,response);
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            HttpHeaders headers = new DefaultHttpHeaders();
            headers.set("Access-Control-Allow-Origin","*");
            headers.set("Access-Control-Allow-Methods","GET, POST, OPTIONS");
            headers.set("Access-Control-Allow-Headers","DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization");
            handshaker.handshake(ctx.channel(), request,headers,ctx.channel().newPromise());
        }
    }
    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                          FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("websocket err",cause);
        ctx.close();
    }


    public void setDispatcher(WebSocketDispatcher webSocketDispatcher) {
        this.webSocketDispatcher = webSocketDispatcher;
    }
}
