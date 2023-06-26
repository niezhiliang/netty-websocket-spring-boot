package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.netty.AttributeKeyConstant;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.core.MethodParameter;

import java.util.Objects;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class HttpHeadersMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Objects.equals(parameter.getParameterType(), HttpHeaders.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel) {
        FullHttpRequest fullHttpRequest = channel.attr(AttributeKeyConstant.fullHttpRequest).get();
        return Objects.nonNull(fullHttpRequest) ? fullHttpRequest.headers() : null;
    }
}
