package com.study.rabbitmq.receiver;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.study.rabbitmq.web.bean.entity.MqMsg;
import com.study.rabbitmq.web.mapper.MqMsgMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * RabbitMQ中的消费者，接收first RabbitMQ中的队列hello1的数据
 */
@Component
public class Receiver {

    @Resource
    private MqMsgMapper mapper;

    //@RabbitListener(queues = "first", containerFactory = "firstFactory")
    @RabbitHandler
    public void process(String msg) {
        final JSONObject object = JSONUtil.parseObj(msg);
        final String context = object.getStr("context");
        System.out.println("Receiver : " + context);
        final MqMsg mqMsg = new MqMsg();
        mqMsg.setContext(context);
        mapper.insert(mqMsg);
    }

}