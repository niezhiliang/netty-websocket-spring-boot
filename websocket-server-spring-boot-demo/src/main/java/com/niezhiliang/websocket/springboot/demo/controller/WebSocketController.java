package com.niezhiliang.websocket.springboot.demo.controller;

import com.niezhiliang.websocket.server.autoconfigure.holder.ChannelHolder;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * websocket
 * 消息推送(个人和广播)
 */
@Controller
public class WebSocketController {

    @Autowired
    private ChannelHolder channelHolder;

    /**
     *
     * 客户端页面
     * @return
     */
    @RequestMapping(value = "/index")
    public String idnex() {

        return "index";
    }

    /**
     *
     * 服务端页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin")
    public String admin(Model model) {

        List<String> list = channelHolder.getAllChannel().stream().map(Channel::id).map(Objects::toString).collect(Collectors.toList());

        model.addAttribute("num",list.size());
        model.addAttribute("users",list);
        return "admin";
    }

    /**
     * 个人信息推送
     * @return
     */
    @RequestMapping("sendmsg")
    @ResponseBody
    public String sendmsg(String msg, String username){
        //第一个参数 :msg 发送的信息内容
        //第二个参数为用户长连接传的用户人数
        String [] persons = username.split(",");
        for (String id : persons) {
            Channel channel = channelHolder.getChannel(id);
            channel.writeAndFlush(new TextWebSocketFrame(msg));
        }
        return "success";
    }

    /**
     * 推送给所有在线用户
     * @return
     */
    @RequestMapping("sendAll")
    @ResponseBody
    public String sendAll(String msg){
        channelHolder.getAllChannel().forEach(e -> e.writeAndFlush(new TextWebSocketFrame(msg)));
        return "success";
    }
}
