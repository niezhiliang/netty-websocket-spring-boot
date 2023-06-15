package com.niezhiliang.websocket.server.autoconfigure.netty;

import com.niezhiliang.websocket.server.autoconfigure.WebsocketProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author nzl
 * @date 2023/6/14
 */
public class InitializerHandler extends ChannelInitializer<SocketChannel> {

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private ConnecteHandler handshakeHandler;

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
                .addLast(messageHandler);
    }
}
