package com.niezhiliang.websocket.client.springboot.starter.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author nzl
 * @date 2023/6/16
 */
@Slf4j
@Getter
public class WebsocketClient implements ApplicationListener<ContextRefreshedEvent> {

    private String host = "127.0.0.1";

    private Integer port = 10086;

    private String wsUri = "ws://localhost:10086/websocket";

    private Integer maxContentLength = 65536;

    private volatile boolean closed = false;

    private volatile boolean closing = false;

    private Channel channel;

    private final EventLoopGroup group = new NioEventLoopGroup(2);

    @SneakyThrows
    private void start() {
        WebsocketClient client = this;
        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(URI.create(wsUri), WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        WsClientHandshakeHandler wsClientHandshakeHandler = new WsClientHandshakeHandler(webSocketClientHandshaker);
        Bootstrap bootstrap = new Bootstrap();
        channel = bootstrap.option(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)
                            .option(ChannelOption.TCP_NODELAY,Boolean.TRUE)
                            .group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<NioSocketChannel>() {
                                @Override
                                protected void initChannel(NioSocketChannel socketChannel) {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast(new IdleStateHandler(0,30,0))
                                            .addLast(new ClientConnectHandler(client))
                                            .addLast(new HttpClientCodec())
                                            .addLast(new HttpObjectAggregator(maxContentLength))
                                            .addLast(wsClientHandshakeHandler)
                                            .addLast(new ClientMessageHandler());
                                }
                            })
                            .connect(new InetSocketAddress(host,port))
                            .sync()
                            .channel();
        wsClientHandshakeHandler.handshakeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                log.info("客户端：{} 握手成功，开始发送心跳",channelFuture.channel().id().asShortText());
                scheduleHeartbeat(channel);
            }
        }).sync();
        channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> close());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.start();
    }

    /**
     * 优雅关闭客户端连接
     */
    public void close() {
        if (closing || closed) {
            return;
        }
        closing = true;
        channel.close();
        group.shutdownGracefully();
        log.info("客户端关闭连接：{}",channel.id().asShortText());

        closing = false;
        closed = true;
    }


    /**
     * 定时向服务端发送心跳包
     * @param channel
     */
    private static void scheduleHeartbeat(Channel channel) {
        channel.eventLoop().scheduleAtFixedRate(() -> {
            channel.writeAndFlush(new PingWebSocketFrame());
        }, 0, 30, TimeUnit.SECONDS);
    }
}
