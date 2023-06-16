package com.niezhiliang.websocket.server.autoconfigure;

import com.niezhiliang.websocket.server.autoconfigure.holder.ChannelHolder;
import com.niezhiliang.websocket.server.autoconfigure.holder.LocalHolderStrategy;
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
    public ServerConnecteHandler connecteHandler() {
        return new ServerConnecteHandler();
    }

    @Bean
    public ChannelHolder channelHolder() {
        return new LocalHolderStrategy();
    }

    @Bean
    public ServerInitializerHandler initializerHandler() {
        return new ServerInitializerHandler();
    }

    @Bean
    public ServerMessageHandler messageHandler() {
        return new ServerMessageHandler();
    }

    @Bean
    public WebsocketServer websocketServer() {
        return new WebsocketServer();
    }

}
