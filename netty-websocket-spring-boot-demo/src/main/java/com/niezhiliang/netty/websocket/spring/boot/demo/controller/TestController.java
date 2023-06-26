package com.niezhiliang.netty.websocket.spring.boot.demo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : niezhiliang
 * @Date : 2023/6/23
 */
@RestController
public class TestController {

    @RequestMapping(value = "/test")
    public String test() {

        return "success";
    }

    @RequestMapping(value = "/test2/{userid}")
    public String test2(String a,@PathVariable("userid") String userid) {

        return "success";
    }
}
