package com.niezhiliang.websocket.server.autoconfigure.listener;

import com.niezhiliang.websocket.server.autoconfigure.netty.WebsocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author nzl
 * @version v
 * @date 2023/6/15
 */
public class StarterEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private WebsocketServer websocketServer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        websocketServer.start();
    }
}
