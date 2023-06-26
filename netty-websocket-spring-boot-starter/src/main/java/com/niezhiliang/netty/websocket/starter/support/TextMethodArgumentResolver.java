package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.annotations.OnMessage;
import com.niezhiliang.netty.websocket.starter.netty.AttributeKeyConstant;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.core.MethodParameter;

import java.util.Objects;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class TextMethodArgumentResolver implements MethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnMessage.class)
                && Objects.equals(parameter.getParameterType(),String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel) {
        TextWebSocketFrame text = channel.attr(AttributeKeyConstant.textWebSocketFrame).get();
        return text.text();
    }
}
