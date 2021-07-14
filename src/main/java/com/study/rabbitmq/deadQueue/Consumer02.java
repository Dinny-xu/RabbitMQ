package com.study.rabbitmq.deadQueue;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;

/**
 * 死信队列消费者02
 */
public class Consumer02 {

    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        String deadQueue = "dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "lisi");
        System.out.println("等待接收死信队列消息....");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("Consumer02接收到消息:" + message);
        };
        channel.basicConsume(deadQueue,true, deliverCallback, consumerTag -> {
        });
    }
}
