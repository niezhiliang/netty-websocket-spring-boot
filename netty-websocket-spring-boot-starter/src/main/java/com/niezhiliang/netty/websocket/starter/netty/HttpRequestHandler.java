package com.niezhiliang.netty.websocket.starter.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * @author nzl
 * @date 2023/6/25
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final WebsocketActionDispatch websocketActionDispatch;

    public HttpRequestHandler(WebsocketActionDispatch websocketActionDispatch) {
        this.websocketActionDispatch = websocketActionDispatch;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 校验请求路径
        boolean pass = verifyRequest(request);
        if (!pass) {
            ctx.close();
        }
        // 参数传递到WebsocketHandler
        ctx.channel().attr(AttributeKeyConstant.fullHttpRequest).set(request);
        ctx.channel().attr(AttributeKeyConstant.PATH_KEY).set(request.uri());
        Map<String, String> uriTemplateVariables = websocketActionDispatch.getUriTemplateVariables(request.uri());
        ctx.channel().attr(AttributeKeyConstant.uriTemplateVariables).set(uriTemplateVariables);
        websocketActionDispatch.dispatch(request.uri(), WebsocketActionDispatch.Action.HAND_SHAKE, ctx.channel());
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true, 65536);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request).addListener(future -> {
                if (future.isSuccess()) {
                    websocketActionDispatch.dispatch(request.uri(), WebsocketActionDispatch.Action.OPEN,ctx.channel());
                } else {
                    handshaker.close(ctx.channel(), new CloseWebSocketFrame());
                }
            });
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }

    /**
     * 验证请求是否是Http升级Websocket
     * 并且验证uri是否合法
     * @param request
     * @return
     */
    private boolean verifyRequest(FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        String connection = headers.get("Connection");
        String upgrade = headers.get("Upgrade");
        String host = headers.get("Host");
        if (Objects.isNull(connection) || Objects.isNull(upgrade) || Objects.isNull(host)) {
            return false;
        } else if (!"Upgrade".equalsIgnoreCase(connection) || !"websocket".equalsIgnoreCase(upgrade))  {
            return false;
        } else if (!"GET".equalsIgnoreCase(request.method().name())) {
            return false;
        }
        return websocketActionDispatch.verifyUri(request.uri());
    }
}
