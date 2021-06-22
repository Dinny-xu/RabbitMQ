package com.study.rabbitmq.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


/**
 * RabbitMQ中的生产者，发送消息到second RabbitMQ中的队列hello1和hello2
 */
//@Component
@RestController
public class SecondSender {

    @Resource(name = "secondRabbitTemplate")
    private RabbitTemplate secondRabbitTemplate;

    @GetMapping("send2")
    public void send1() {
        String context = "1次发送 " + new Date();
        System.out.println("Sender : " + context);
        this.secondRabbitTemplate.convertAndSend("second","secondRoutingKey", context);
    }

}