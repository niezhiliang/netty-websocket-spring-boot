package com.niezhiliang.websocket.server.autoconfigure;

import com.niezhiliang.websocket.server.autoconfigure.holder.ChannelHolder;
import com.niezhiliang.websocket.server.autoconfigure.holder.LocalHolderStrategy;
import com.niezhiliang.websocket.server.autoconfigure.listener.StarterEventListener;
import com.niezhiliang.websocket.server.autoconfigure.netty.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nzl
 * @date 2023/6/14
 */
@Configuration
@ConditionalOnProperty(prefix = "ws.server",name = "enable",havingValue = "true")
public class WebSocketServerAutoConfiguration {

    @Bean
    public WebsocketProperties websocketProperties() {
        return new WebsocketProperties();
    }

    @Bean
    public ConnecteHandler connecteHandler() {
        return new ConnecteHandler();
    }

    @Bean
    public HeatBeatHandler heatBeatHandler() {
        return new HeatBeatHandler();
    }

    @Bean
    public ChannelHolder channelHolder() {
        return new LocalHolderStrategy();
    }

    @Bean
    public InitializerHandler initializerHandler() {
        return new InitializerHandler();
    }

    @Bean
    public MessageHandler messageHandler() {
        return new MessageHandler();
    }

    @Bean
    public WebsocketServer websocketServer() {
        return new WebsocketServer();
    }

    @Bean
    public StarterEventListener starterEventListener() {
        return new StarterEventListener();
    }

}
