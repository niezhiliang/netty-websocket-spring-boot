package com.niezhiliang.websocket.server.autoconfigure.holder;

import io.netty.channel.Channel;

import java.util.Collection;

/**
 * @author nzl
 * @date 2023/6/16
 */
public class DistributedHolderStrategy implements ChannelHolder{
    @Override
    public boolean addChannel(String id, Channel ctx) {
        return false;
    }

    @Override
    public boolean removeChannel(String... id) {
        return false;
    }

    @Override
    public Collection<Channel> getAllChannel() {
        return null;
    }

    @Override
    public Channel getChannel(String id) {
        return null;
    }
}
