package com.study.rabbitmq.web.controller;



import com.study.rabbitmq.web.bean.entity.MqMsg;
import com.study.rabbitmq.web.mapper.MqMsgMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RabbitMqController {


    @Resource
    private MqMsgMapper mapper;


    @GetMapping("/test")
    public void insert() {
        final MqMsg mqMsg = new MqMsg();
        mqMsg.setContext("一个");
        mapper.insert(mqMsg);
    }

}
