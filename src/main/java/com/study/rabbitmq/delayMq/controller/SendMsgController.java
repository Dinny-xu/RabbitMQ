package com.study.rabbitmq.delayMq.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 消息生产者
 */
@RestController
@Slf4j
public class SendMsgController {

    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";

    public static final String DELAYED_ROUTING_KEY = "delayed.routingKey";

    @Resource
    private RabbitTemplate rabbitTemplate;


    @GetMapping("sendMsg/{message}")
    public void senMsg(@PathVariable String message) {
        log.info("当前时间{},发生一条消息给两个TTL队列:{}", new Date(), message);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl为10s的队列" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl为40s的队列" + message);
    }


    /*
     * 发送两条消息->
     *  消息1：TTL -> 20000
     *  消息2：TTL -> 2000
     *  按照TTL 消费时长，2000应该会被提前消费，但是结果确是消息1被提前消费
     *  由于死信队列延迟消费并不会根据消息的时长进行优先级消费，故而消息1,消息2 最终会被同时消费
     *
     *  如果使用在消息属性上设置 TTL 的方式，消息可能并不会按时“死亡“，因为 RabbitMQ 只会检查第一个消息是否过期，过期则丢到死信队列，
     *  如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
     *
     */
    @GetMapping("/sendMsg/{message}/{ttlTime}")
    public void senMsg(@PathVariable String message, @PathVariable String ttlTime) {
        log.info("当前时间:{},发送一条时长{}毫秒TTL 信息给队列QC:{}", new Date(), ttlTime, message);
        rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
            //设置消息延迟时长-->
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }


    /*
     * 开始发消息 -> 基于插件的消息及延迟时间
     * 使用插件对交换机进行延迟队列接收，收到的消息时间短的将会先被消费
     */
    @GetMapping("/sendDelayed/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message, @PathVariable Integer delayTime) {
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE_NAME, DELAYED_ROUTING_KEY, message, correlationData -> {
            correlationData.getMessageProperties().setDelay(delayTime);
            return correlationData;
        });
        log.info("当前时间:{},发送一条延迟{}毫秒的信息给队列delayed.queue:{}", new Date(), delayTime, message);
    }
}
