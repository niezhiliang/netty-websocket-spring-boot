package com.niezhiliang.netty.websocket.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nzl
 * @date 2023/6/26
 */
@ConfigurationProperties(prefix = WebsocketProperties.WEBSOCKET_PREFIX)
@Data
public class WebsocketProperties {

    public static final String WEBSOCKET_PREFIX = "netty.websocket";

    private Integer port;

    private Integer bossThreadNums = 1;

    private Integer workerThreadNums = 2;

    /**
     * 连接超时时间
     */
    private Integer connectTimeout = 15000;
    /**
     * TCP 连接的请求队列的最大长度,默认128
     */
    private Integer backLog = 128;

    /**
     * 消息是否立即发送
     */
    private boolean tcpNoDelay = true;

    /**
     * 心跳读超时时间
     */
    private Integer readerIdleTimeSeconds = 60;

    /**
     * 心跳写超时时间
     */
    private Integer writerIdleTimeSeconds = 60;

    private Integer allIdleTimeSeconds = 60;

    /**
     *
     */
    private Integer maxContentLength = 65536;
}
