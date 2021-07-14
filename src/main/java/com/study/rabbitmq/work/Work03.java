package com.study.rabbitmq.work;


import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

/**
 * 消息应答模式 -> 消费者01
 */
public class Work03 {

    public static final String ASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        System.out.println("C1 等待接收消息处理时间较短");
        //消息消费的时候如何处理
        DeliverCallback deliverCallback = ((consumerTag, delivery) -> {
            val message = new String(delivery.getBody());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("接收到消息：" + message);
            /*
             * 消息标记 tag
             * 是否批量应答未应答的消息
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        });
        //采用不公平分发原则-> 谁处理消息快，就处理的多
        channel.basicQos(1);
        //手动应答
        boolean autoAck = false;
        channel.basicConsume(ASK_QUEUE_NAME, autoAck, deliverCallback, (consumerTag -> System.out.println(consumerTag + "消费者取消消费接口回调逻辑")));
    }
}
