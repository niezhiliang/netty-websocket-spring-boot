package com.niezhiliang.netty.websocket.starter.netty;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class AttributeKeyConstant {
    public static final AttributeKey<String> PATH_KEY = AttributeKey.valueOf("WEBSOCKET_PATH");

    public static final AttributeKey<FullHttpRequest> fullHttpRequest = AttributeKey.valueOf("FullHttpRequest");
    public static AttributeKey<TextWebSocketFrame> textWebSocketFrame = AttributeKey.valueOf("TextWebSocketFrame");

    public static AttributeKey<Object> idleStateEvent = AttributeKey.valueOf("IdleStateEvent");

    public static AttributeKey<Throwable> throwable = AttributeKey.valueOf("Throwable");

    public static AttributeKey<Map<String,String>> uriTemplateVariables = AttributeKey.valueOf("uriTemplateVariables");
}
