package com.niezhiliang.netty.websocket.spring.boot.demo;

import com.niezhiliang.netty.websocket.spring.boot.demo.service.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author : niezhiliang
 * @Date : 2023/6/23
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args)  {
        SpringApplication.run(Application.class).getBean(ServerEndpoint.class);
    }
}
