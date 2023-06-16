package com.example.clientdemo;

import com.niezhiliang.websocket.server.autoconfigure.netty.client.WebsocketClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientDemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(ClientDemoApplication.class, args);
        WebsocketClient client = new WebsocketClient("127.0.0.1", 10086, "ws://127.0.0.1:10086/websocket");
        client.connect();

        while (true){

        }
    }

}
