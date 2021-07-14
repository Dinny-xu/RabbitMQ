package com.study.rabbitmq.work;

import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

/**
 * 消息应答模式 -> 消费者02
 */
public class Work04 {

    /*
     * 一个生产者Task02
     * 两个消费者 work03-work04
     * 使用手动应答模式，进行消息中断自动重新入队再次被其它消费者消费
     * 开启work03-work04
     * 同时发送2条消息
     * 第一条被work03消费，此时关闭work04消费者，第二条原本应该被work04消费的消息将重新入队被work03消费
     */
    public static final String ASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        System.out.println("C2 等待接收消息处理时间较长");
        DeliverCallback deliverCallback = ((consumerTag, delivery) -> {
            val message = new String(delivery.getBody());
            try {
                Thread.sleep(1000 * 15);
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
        /*
         * 0:正常分发消息
         * 1:不公平分发原则-> 谁处理消息快，就处理的多
         * 大于1: 预期值分发，例如2个work总消息10条,A预期值8，B预期值2 ,那A消费8条，B消费2条
         */
        int prefetchCount = 0;

        channel.basicQos(prefetchCount);
        boolean autoAck = false;
        channel.basicConsume(ASK_QUEUE_NAME, autoAck, deliverCallback, (consumerTag -> System.out.println(consumerTag + "消费者取消消费接口回调逻辑")));
    }
}
