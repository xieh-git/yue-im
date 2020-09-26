package com.yueya.im.server.netty;

import com.yueya.im.common.api.ImSetting;
import com.yueya.im.server.handler.TcpServerInitializer;
import com.yueya.im.server.handler.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * boss 线程组用于处理连接工作
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * work 线程组用于数据处理
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private WebSocketServerInitializer webSocketServerInitializer = new WebSocketServerInitializer();
    private TcpServerInitializer tcpServerInitializer = new TcpServerInitializer();
    public void start(ImSetting setting,int port,String mode) {
        try {
            initHandler(setting);
            ServerBootstrap bootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO));
            if("tcp".equals(mode)){
                bootstrap = bootstrap.childHandler(tcpServerInitializer);
            }else if("websocket".equals(mode)){
                bootstrap = bootstrap.childHandler(webSocketServerInitializer);
            }else{
                throw new IllegalArgumentException("不兼容的协议模式:"+mode);
            }
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
            if (future.isSuccess()) {
                logger.info("启动 Netty Server:"+port);
            }
        } catch (InterruptedException e) {
            logger.error("netty运行异常",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void destory() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        logger.info("关闭Netty");
    }


    public void initHandler(ImSetting setting) {
        webSocketServerInitializer.setHandlers(setting.getHandlerMap(),setting.getBusinessInfoProvider());
        webSocketServerInitializer.setHearBeat(setting.getHearBeatConfig());
        tcpServerInitializer.setHandlers(setting.getHandlerMap(),setting.getBusinessInfoProvider());
        tcpServerInitializer.setHearBeat(setting.getHearBeatConfig());
    }

}
