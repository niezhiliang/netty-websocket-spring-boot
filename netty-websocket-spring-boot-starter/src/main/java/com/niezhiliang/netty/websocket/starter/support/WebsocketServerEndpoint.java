package com.niezhiliang.netty.websocket.starter.support;

import com.niezhiliang.netty.websocket.starter.annotations.*;
import com.niezhiliang.netty.websocket.starter.exception.WebsocketDeploymentException;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author : niezhiliang
 * @Date : 2023/6/23
 */
@Getter
public class WebsocketServerEndpoint {

    /**
     * @WsServerEndpoint配置的路径
     */
    private String path;

    /**
     * 握手前调用的目标方法
     */
    private Method onHandShake;

    /**
     * 连接关闭事件调用的目标方法
     */
    private Method onClose;

    /**
     * 触发心跳事件调用的目标方法
     */
    private Method onEvent;

    /**
     * 连接成功调用的目标方法
     */
    private Method onOpen;

    /**
     * 收到消息调用的目标方法
     */
    private Method onMessage;

    /**
     * 错误事件调用的目标方法
     */
    private Method onError;

    /**
     * path对应@WsServerEndpoint修饰的类
     */
    private Object object;

    public WebsocketServerEndpoint(Class<?> pojoClazz,Object o,String path) {
        this.object = o;
        this.path = path;
        AtomicReference<Method> handShake = new AtomicReference<>();
        AtomicReference<Method> close = new AtomicReference<>();
        AtomicReference<Method> event = new AtomicReference<>();
        AtomicReference<Method> open = new AtomicReference<>();
        AtomicReference<Method> message = new AtomicReference<>();
        AtomicReference<Method> error = new AtomicReference<>();

        Method[] pojoClazzMethods = null;
        Class<?> currentClazz = pojoClazz;
        while (!currentClazz.equals(Object.class)) {
            Method[] currentClazzMethods = currentClazz.getDeclaredMethods();
            if (currentClazz == pojoClazz) {
                pojoClazzMethods = currentClazzMethods;
            }
            for (Method method : currentClazzMethods) {
                if (Objects.nonNull(method.getAnnotation(HandshakeBefore.class))) {
                    methodFill(currentClazz,method,pojoClazz,handShake, HandshakeBefore.class);
                } else if (Objects.nonNull(method.getAnnotation(OnClose.class))) {
                    methodFill(currentClazz,method,pojoClazz,close,OnClose.class);
                } else if (Objects.nonNull(method.getAnnotation(OnEvent.class))) {
                    methodFill(currentClazz,method,pojoClazz,event,OnEvent.class);
                } else if (Objects.nonNull(method.getAnnotation(OnOpen.class))) {
                    methodFill(currentClazz,method,pojoClazz,open,OnOpen.class);
                } else if (Objects.nonNull(method.getAnnotation(OnMessage.class))) {
                    methodFill(currentClazz,method,pojoClazz,message,OnMessage.class);
                } else if (Objects.nonNull(method.getAnnotation(OnError.class))) {
                    methodFill(currentClazz,method,pojoClazz,error,OnError.class);
                }
            }
            currentClazz = currentClazz.getSuperclass();
            this.onHandShake = handShake.get();
            this.onClose = close.get();
            this.onEvent = event.get();
            this.onOpen = open.get();
            this.onMessage = message.get();
            this.onError = error.get();
        }
    }


    private void methodFill(Class<?> currentClazz, Method method, Class<?> pojoClazz, AtomicReference<Method> point, Class annotation) {
        checkPublic(method);
        if (Objects.isNull(point.get())) {
            point.set(method);
        } else {
            if (currentClazz == pojoClazz ||
                    !isMethodOverride(point.get(), method)) {
                throw new WebsocketDeploymentException(
                        "wsServerEndpoint.duplicateAnnotation " + annotation.getSimpleName());
            }
        }
    }

    /**
     * 判断方法是否public
     * @param m
     * @throws WebsocketDeploymentException
     */
    private void checkPublic(Method m) throws WebsocketDeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new WebsocketDeploymentException(
                    "pojoMethodMapping.methodNotPublic " + m.getName());
        }
    }

    /**
     * 判断方法是否重写方法
     * @param method1
     * @param method2
     * @throws WebsocketDeploymentException
     */
    private boolean isMethodOverride(Method method1, Method method2) {
        return (method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes()));
    }
}
