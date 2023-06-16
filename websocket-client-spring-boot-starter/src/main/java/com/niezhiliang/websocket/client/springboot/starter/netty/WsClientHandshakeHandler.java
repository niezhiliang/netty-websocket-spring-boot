package com.niezhiliang.websocket.client.springboot.starter.netty;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nzl
 * @date 2023/6/16
 */
@Slf4j
public class WsClientHandshakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接处理器
     */
    private final WebSocketClientHandshaker webSocketClientHandshaker;

    /**
     * netty提供的数据过程中的数据保证
     */
    private ChannelPromise handshakeFuture;

    private Channel channel;

    public WsClientHandshakeHandler(WebSocketClientHandshaker webSocketClientHandshaker) {
        this.webSocketClientHandshaker = webSocketClientHandshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    /**
     * ChannelHandler添加到实际上下文中准备处理事件,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    /**
     * 当客户端主动链接服务端的链接后,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        webSocketClientHandshaker.handshake(channel);
        log.debug("建立连接 {}", ctx.channel());
    }


    /**
     * 接收消息,调用此方法
     *
     * @param ctx ChannelHandlerContext
     * @param msg Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!webSocketClientHandshaker.isHandshakeComplete()) {
            this.handleHttpRequest(msg);
            log.info("websocket建立连接完毕 {}", ctx.channel());
            return;
        }
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 处理http连接请求.<br>
     *
     * @param msg:
     * @return:
     */
    private void handleHttpRequest(Object msg) {
        webSocketClientHandshaker.finishHandshake(channel, (FullHttpResponse) msg);
        handshakeFuture.setSuccess();
    }

}
