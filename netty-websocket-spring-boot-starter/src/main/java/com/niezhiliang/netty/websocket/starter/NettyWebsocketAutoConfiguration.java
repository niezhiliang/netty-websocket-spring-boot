package com.niezhiliang.netty.websocket.starter;

import com.niezhiliang.netty.websocket.starter.support.WebSocketAnnotationPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : niezhiliang
 * @Date : 2023/6/23
 */
@Configuration
public class NettyWebsocketAutoConfiguration {

    @Bean
    public WebSocketAnnotationPostProcessor webSocketAnnotationPostProcessor() {
        return new WebSocketAnnotationPostProcessor();
    }

    @Bean
    public WebsocketProperties websocketProperties() {
        return new WebsocketProperties();
    }
}
