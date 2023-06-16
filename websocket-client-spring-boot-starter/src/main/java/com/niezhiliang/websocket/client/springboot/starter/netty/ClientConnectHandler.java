package com.niezhiliang.websocket.client.springboot.starter.netty;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nzl
 * @date 2023/6/16
 */
@ChannelHandler.Sharable
@Slf4j
public class ClientConnectHandler extends ChannelInboundHandlerAdapter {

    private final WebsocketClient client;

    public ClientConnectHandler(WebsocketClient client) {
        this.client = client;

    }

    /**
     * 关闭连接
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.warn("客户端断开连接 移除连接的服务器 关闭通道{}", ctx.channel());
        client.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端连接发生错误 {} {}", ctx.channel(), cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 删除无用通道
                log.warn("未及时发送心跳 客户端移除已经连接的服务器 同时关闭连接 {}", ctx.channel());
                client.close();
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
