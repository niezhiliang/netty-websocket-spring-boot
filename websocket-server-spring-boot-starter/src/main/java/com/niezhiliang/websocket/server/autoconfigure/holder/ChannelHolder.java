package com.niezhiliang.websocket.server.autoconfigure.holder;

import io.netty.channel.Channel;

import java.util.Collection;

/**
 * @author nzl
 * @date 2023/6/14
 */
public interface ChannelHolder {

    boolean addChannel(String id, Channel ctx);

    boolean removeChannel(String ...id);

    Collection<Channel> getAllChannel();

    Channel getChannel(String id);

}
