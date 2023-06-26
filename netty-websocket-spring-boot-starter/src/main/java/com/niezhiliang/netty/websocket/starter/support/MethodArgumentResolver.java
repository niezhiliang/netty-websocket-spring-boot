package com.niezhiliang.netty.websocket.starter.support;

import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/**
 * @author nzl
 * @date 2023/6/26
 */
public interface MethodArgumentResolver {


    boolean supportsParameter(MethodParameter parameter);

    @Nullable
    Object resolveArgument(MethodParameter parameter, Channel channel);
}
