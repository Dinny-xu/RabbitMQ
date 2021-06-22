package com.study.rabbitmq.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Component;


/**
 * RabbitMQ中的消费者，接收second RabbitMQ中的队列hello2的数据
 */
@Component
public class Receiver2 {


    //@RabbitListener(queues = "second", containerFactory = "secondFactory")
    @RabbitHandler
    public void process(String msg) {
        System.out.println("Receiver : " + msg);
    }

}