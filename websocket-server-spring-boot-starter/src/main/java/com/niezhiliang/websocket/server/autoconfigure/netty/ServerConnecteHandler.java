package com.niezhiliang.websocket.server.autoconfigure.netty;

import com.niezhiliang.websocket.server.autoconfigure.holder.ChannelHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author nzl
 * @date 2023/6/14
 */
@ChannelHandler.Sharable
@Slf4j
public class ServerConnecteHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ChannelHolder channelHolder;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            log.info("握手请求：{}",ctx.channel().id());
            channelHolder.addChannel(ctx.channel().id().asShortText(),ctx.channel());
        }
        super.channelRead(ctx, msg);
        if (msg instanceof FullHttpRequest) {
            ctx.writeAndFlush(new TextWebSocketFrame(ctx.channel().id().asShortText()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                // 删除无用通道
                ctx.close();
                channelHolder.removeChannel(ctx.channel().id().asShortText());
                log.info("未及时发送心跳 关闭连接 {}", ctx.channel());
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelHolder.removeChannel(ctx.channel().id().asShortText());
        log.info("断开连接:{}",ctx.channel().id());
        super.channelInactive(ctx);
    }
}
