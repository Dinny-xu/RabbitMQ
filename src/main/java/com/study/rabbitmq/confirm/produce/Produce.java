package com.study.rabbitmq.confirm.produce;


import com.study.rabbitmq.confirm.MyCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * 发布确认高级-消息生产者
 */
@RestController
@RequestMapping("/confirm")
@Slf4j
public class Produce {

    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private MyCallBack myCallBack;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(myCallBack);
        rabbitTemplate.setReturnCallback(myCallBack);
    }


    @GetMapping("sendMessage/{message}")
    public void sendMessage(@PathVariable String message) {
        //正确消息
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME, "key1", message + "key1", correlationData1);
        log.info("发送消息内容:{}", message + "key1");

        //错误消息 -> 设置mandatory后,未发送成功的消息将被回退
        CorrelationData correlationData2 = new CorrelationData("2");
        rabbitTemplate.convertAndSend(CONFIRM_EXCHANGE_NAME, "key2", message + "key2", correlationData2);
        log.info("发送消息内容:{}", message + "key2");
    }

}
