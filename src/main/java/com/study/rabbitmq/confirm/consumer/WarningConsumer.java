package com.study.rabbitmq.confirm.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 备份交换机-报警消费者
 */
@Component
@Slf4j
public class WarningConsumer {

    public static final String WARNING_QUEUE_NAME = "warning.queue";

    @RabbitListener(queues = WARNING_QUEUE_NAME)
    public void receiveMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("报警发现不可路由消息:{}", msg);
    }
}
