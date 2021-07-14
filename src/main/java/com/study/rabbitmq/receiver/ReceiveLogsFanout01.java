package com.study.rabbitmq.receiver;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

/**
 * 扇形交换机->一个队列进行广播发送
 */
public class ReceiveLogsFanout01 {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        /*
         * 生成一个临时的队列，队列名称是随机的
         * 当消费者断开和该队列的连接时，队列自动删除
         */
        val queueName = channel.queueDeclare().getQueue();
        //把该临时队列绑定我们的 exchange 其中routingKey (也称为binding key) 为空字符串
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息，把接收的消息打印在屏幕...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("控制台打印接收到的消息" + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
