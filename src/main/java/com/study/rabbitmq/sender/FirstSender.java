package com.study.rabbitmq.sender;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;


/**
 * RabbitMQ中的生产者，发送消息到first RabbitMQ中的队列hello1和hello2
 */
@Component
public class FirstSender {

    @Resource(name="firstRabbitTemplate")
    private RabbitTemplate firstRabbitTemplate;

    public void send1() {
        final HashMap<Object, Object> map = MapUtil.newHashMap();
        map.put("context", "一条消息");
        final String msg = JSONUtil.toJsonStr(map);
        this.firstRabbitTemplate.convertAndSend("first","firstDirectRouting", msg);
    }
}