package com.niezhiliang.websocket.server.autoconfigure.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nzl
 * @date 2023/6/14
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, WebSocketFrame frame) {
        if (frame instanceof PingWebSocketFrame) {
            pingWebSocketFrameHandler(context, (PingWebSocketFrame) frame);
        } else if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            System.out.println(text);
        } else if (frame instanceof CloseWebSocketFrame) {
            log.warn("clientId: {} 断开连接",context.channel().id());
        } else if (frame instanceof BinaryWebSocketFrame) {

        }
    }


    /**
     * 处理客户端心跳包
     *
     * @param ctx 通道上下文
     * @param frame 关闭消息体
     */
    private void pingWebSocketFrameHandler(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        log.info("客户端发送心跳请求 {} {}", ctx.channel(), frame);
        ctx.channel().writeAndFlush(new PongWebSocketFrame());
    }
}
