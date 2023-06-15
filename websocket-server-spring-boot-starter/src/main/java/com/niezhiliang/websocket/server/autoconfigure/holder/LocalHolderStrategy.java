package com.niezhiliang.websocket.server.autoconfigure.holder;

import io.netty.channel.Channel;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author nzl
 * @date 2023/6/14
 */
public class LocalHolderStrategy implements ChannelHolder {

    private static ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>(32);

    @Override
    public boolean addChannel(String id, Channel ctx) {
         channelMap.put(id,ctx);
         return true;
    }

    @Override
    public boolean removeChannel(String... id) {
        Arrays.stream(id).forEach(e -> channelMap.remove(e));
        return true;
    }

    @Override
    public Collection<Channel> getAllChannel() {
        return channelMap.values();
    }

    @Override
    public Channel getChannel(String id) {
        return channelMap.get(id);
    }
}
