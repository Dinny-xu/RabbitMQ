package com.study.rabbitmq.confirm.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 发布确认高级-消息消费者
 */
@Component
@Slf4j
public class ConfirmConsumer {

    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";


    @RabbitListener(queues = CONFIRM_QUEUE_NAME)
    public void receiveMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("接收到队列confirm.queue 消息:{}", msg);
    }


}
