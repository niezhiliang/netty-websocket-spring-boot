package com.niezhiliang.websocket.server.autoconfigure.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nzl
 * @date 2023/6/16
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) msg;
            log.info("收到服务器消息 {} {}", ctx.channel(), textFrame.text());
        } else if (msg instanceof CloseWebSocketFrame) {
            log.info("连接收到关闭帧 {}", ctx.channel());
            ctx.channel().close();
        } else if (msg instanceof PongWebSocketFrame) {
            log.info("收到服务器心跳响应 {} {}", ctx.channel(), msg);
        }
    }
}
