package com.niezhiliang.websocket.client.springboot.starter;

import com.niezhiliang.websocket.client.springboot.starter.netty.WebsocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nzl
 * @date 2023/6/16
 */
@Configuration
public class WebSocketClientAutoConfiguration {

    @Bean
    public WebsocketClient websocketClient() {
        return new WebsocketClient();
    }
}
