package com.study.rabbitmq.receiver;

import cn.hutool.core.io.FileUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.io.File;


/**
 * 扇形交换机->一个队列进行广播发送
 */
public class ReceiveLogsFanout02 {

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
        System.out.println("等待接收消息，把接收的消息写入文件.....");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            File file = new File("/Users/dinny-xu/Desktop/rabbitmq_info.txt");
            FileUtil.appendUtf8String(message, file);
            System.out.println("文件写入成功");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
