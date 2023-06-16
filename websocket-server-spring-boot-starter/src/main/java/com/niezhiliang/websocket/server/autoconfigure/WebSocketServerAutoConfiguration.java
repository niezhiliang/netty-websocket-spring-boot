package com.niezhiliang.websocket.server.autoconfigure;

import com.niezhiliang.websocket.server.autoconfigure.holder.ChannelHolder;
import com.niezhiliang.websocket.server.autoconfigure.holder.DistributedHolderStrategy;
import com.niezhiliang.websocket.server.autoconfigure.holder.LocalHolderStrategy;
import com.niezhiliang.websocket.server.autoconfigure.netty.server.ServerConnecteHandler;
import com.niezhiliang.websocket.server.autoconfigure.netty.server.ServerInitializerHandler;
import com.niezhiliang.websocket.server.autoconfigure.netty.server.ServerMessageHandler;
import com.niezhiliang.websocket.server.autoconfigure.netty.server.WebsocketServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

//    @Bean
//    @ConditionalOnBean(name = "redisTemplate")
//    public ChannelHolder distributedHolderStrategy() {
//        return new DistributedHolderStrategy();
//    }

    @Bean
    public ChannelHolder localHolderStrategy() {
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
