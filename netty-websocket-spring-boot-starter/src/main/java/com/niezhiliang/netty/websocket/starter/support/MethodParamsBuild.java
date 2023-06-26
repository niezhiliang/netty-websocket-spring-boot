package com.niezhiliang.netty.websocket.starter.support;

import io.netty.channel.Channel;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nzl
 * @date 2023/6/26
 */
public class MethodParamsBuild {

    private static List<MethodArgumentResolver> resolvers = new ArrayList<>(10);

    private volatile ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    static {
        resolvers.add(new SessionMethodArgumentResolver());
        resolvers.add(new PathParaMethodArgumentResolver());
        resolvers.add(new TextMethodArgumentResolver());
        resolvers.add(new IdleEventMethodArgumentResolver());
        resolvers.add(new HttpHeadersMethodArgumentResolver());
    }
    private static final Object[] EMPTY_ARGS = new Object[0];


    public Object[] getMethodArgumentValues(Method method, Channel channel) {
        MethodParameter[] parameters = getMethodParameters(method);
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Object value = null;
            for (MethodArgumentResolver resolver : resolvers) {
                if (resolver.supportsParameter(parameters[i])) {
                    value = resolver.resolveArgument(parameters[i],channel);
                    break;
                }
            }
            args[i] = value;
        }
        return args;
    }

    private MethodParameter[] getMethodParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        MethodParameter[] methodParameters = new MethodParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = new MethodParameter(method,i);
            parameter.initParameterNameDiscovery(parameterNameDiscoverer);
            methodParameters[i] = parameter;
        }
        return methodParameters;
    }
}
