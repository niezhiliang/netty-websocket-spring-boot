package com.niezhiliang.netty.websocket.starter.netty;

import com.niezhiliang.netty.websocket.starter.support.MethodParamsBuild;
import com.niezhiliang.netty.websocket.starter.support.WebsocketServerEndpoint;
import io.netty.channel.Channel;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nzl
 * @date 2023/6/25
 */
public class WebsocketActionDispatch {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final static Map<String, WebsocketServerEndpoint> endpointMap = new ConcurrentHashMap<>(16);


    /**
     * 验证请求路径是否合法
     * @param uri
     * @return
     */
    protected boolean verifyUri(String uri) {
        return endpointMap.keySet().stream().anyMatch(e -> antPathMatcher.match(e, uri));
    }

    /**
     * 添加websocket服务
     * @param endpoint
     */
     public void addWebsocketServerEndpoint(WebsocketServerEndpoint endpoint) {
        endpointMap.putIfAbsent(endpoint.getPath(),endpoint);
    }


    /**
     * uri匹配对应的websocket服务
     * @param uri
     * @return
     */
    protected WebsocketServerEndpoint matchServerEndpoint(String uri) {
        for (Map.Entry<String, WebsocketServerEndpoint> entry : endpointMap.entrySet()) {
            if (antPathMatcher.match(entry.getKey(),uri)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 事件分发到具体的方法
     * @param uri
     * @param action
     */
    protected void dispatch(String uri, Action action, Channel channel) {
        WebsocketServerEndpoint websocketServerEndpoint = matchServerEndpoint(uri);
        if (Objects.nonNull(websocketServerEndpoint)) {
            Method method = null;
            Object obj = websocketServerEndpoint.getObject();
            switch (action) {
                case HAND_SHAKE:
                    method = websocketServerEndpoint.getOnHandShake();
                    break;
                case OPEN:
                    method = websocketServerEndpoint.getOnOpen();
                    break;
                case CLOSE:
                    method = websocketServerEndpoint.getOnClose();
                    break;
                case MESSAGE:
                    method = websocketServerEndpoint.getOnMessage();
                    break;
                case EVENT:
                    method = websocketServerEndpoint.getOnEvent();
                    break;
                case ERROR:
                    method = websocketServerEndpoint.getOnError();
                    break;
                default:
                    break;
            }
            if (Objects.nonNull(method)) {
                Object[] args = new MethodParamsBuild().getMethodArgumentValues(method,channel);
                ReflectionUtils.invokeMethod(method,obj,args);
            }
        }

    }

    public Map<String,String> getUriTemplateVariables(String lookupPath) {
        WebsocketServerEndpoint websocketServerEndpoint = matchServerEndpoint(lookupPath);
        return antPathMatcher.extractUriTemplateVariables(websocketServerEndpoint.getPath(), lookupPath);
    }



     enum Action {
        HAND_SHAKE,
        OPEN,
        CLOSE,
        MESSAGE,
        EVENT,
        ERROR
    }
}
