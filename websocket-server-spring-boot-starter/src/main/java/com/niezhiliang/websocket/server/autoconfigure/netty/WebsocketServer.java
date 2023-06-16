package com.niezhiliang.websocket.server.autoconfigure.netty;

import com.niezhiliang.websocket.server.autoconfigure.WebsocketProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

import java.net.InetAddress;

/**
 * @author nzl
 * @date 2023/6/14
 */
@Slf4j
@Order(Integer.MAX_VALUE -1)
public class WebsocketServer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private WebsocketProperties websocketProperties;

    @Autowired
    private ServerInitializerHandler serverInitializerHandler;

    private String ip;



    @SneakyThrows
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(websocketProperties.getBoosThread());
        NioEventLoopGroup worker = new NioEventLoopGroup(websocketProperties.getWorkerThread());
        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(serverInitializerHandler)
                .option(ChannelOption.SO_BACKLOG,1)
                .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE);

        ChannelFuture channelFuture = serverBootstrap.bind(websocketProperties.getPort()).sync();
        Channel serverChannle = channelFuture.channel();
        this.ip = InetAddress.getLocalHost().getHostAddress();
        log.info("WebSocket服务 ws://{}:{}{} 启动", ip,websocketProperties.getPort(),websocketProperties.getWebSocketPrefix());
        serverChannle.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("服务器关闭...");
            }
        });
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.start();
    }
}
