package com.niezhiliang.websocket.server.autoconfigure.netty.server;

import com.niezhiliang.websocket.server.autoconfigure.WebsocketProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author nzl
 * @date 2023/6/14
 */
public class ServerInitializerHandler extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ServerMessageHandler serverMessageHandler;

    @Autowired
    private ServerConnecteHandler handshakeHandler;

//    @Autowired
//    private HeatBeatHandler heatBeatHandler;

    @Autowired
    private WebsocketProperties websocketProperties;

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateHandler(0,35,0))
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast(handshakeHandler)
//                .addLast(heatBeatHandler)
//                .addLast(new WebSocketServerCompressionHandler())
                .addLast(new WebSocketServerProtocolHandler(websocketProperties.getWebSocketPrefix(),null,true,65535))
                .addLast(serverMessageHandler);
    }
}
