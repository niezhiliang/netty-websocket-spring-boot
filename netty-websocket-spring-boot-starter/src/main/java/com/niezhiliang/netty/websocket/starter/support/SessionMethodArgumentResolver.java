package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.socket.Session;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

import java.util.Objects;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class SessionMethodArgumentResolver implements MethodArgumentResolver{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Objects.equals(Session.class,parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel) {
        return new Session(channel);
    }
}
