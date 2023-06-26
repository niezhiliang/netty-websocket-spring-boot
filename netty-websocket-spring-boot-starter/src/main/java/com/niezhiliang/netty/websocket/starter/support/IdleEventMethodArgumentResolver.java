package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.annotations.OnEvent;
import com.niezhiliang.netty.websocket.starter.netty.AttributeKeyConstant;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

import java.util.Objects;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class IdleEventMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnEvent.class) && Objects.equals(parameter.getParameterType(),Object.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel) {
        return channel.attr(AttributeKeyConstant.idleStateEvent).get();
    }
}
