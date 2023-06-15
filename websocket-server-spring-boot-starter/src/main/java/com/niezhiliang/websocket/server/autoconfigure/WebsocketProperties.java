package com.niezhiliang.websocket.server.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nzl
 * @date 2023/6/14
 */
@ConfigurationProperties(prefix = "ws.server")
@Data
public class WebsocketProperties {

    /**
     * webscoket路径
     */
    private String webSocketPrefix = "/websocket";

    /**
     * 监听端口
     */
    private Integer port = 9889;

    /**
     * boss线程数量
     */
    private Integer boosThread = 1;

    /**
     * worker线程数量
     */
    private Integer workerThread = 2;

}
