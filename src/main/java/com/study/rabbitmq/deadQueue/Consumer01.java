package com.study.rabbitmq.deadQueue;

import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.util.HashMap;


/**
 * 死信队列消费者01
 */
public class Consumer01 {

    //普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    public static final String DEAD_EXCHANGE = "dead_exchange";
    //声明死信队列
    public static final String DEAD_QUEUE = "dead-queue";
    //声明普通队列
    public static final String NORMAL_QUEUE = "normal-queue";


    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        //声明死信交换机和普通交换机类型为直连型-> direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        //死信队列绑定死信交换机与routingKey
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");

        //正常队列绑定死信队列信息
        HashMap<String, Object> params = MapUtil.newHashMap();
        //正常队列设置死信交换机 参数key是固定值
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routingKey 参数key是固定值
        params.put("x-dead-letter-routing-key", "lisi");
        //设置正常队列的长度限制
//        params.put("x-max-length", 6);

        //声明普通队列为死信类型 -> 该队列超过TTL时间后，消息将自动转发到死信队列
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, params);
        //普通队列绑定普通交换机与routingKey
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");

        System.out.println("等待接收消息......");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            //拒接info5的消息
            if (message.equals("info5")) {
                System.out.println("Consumer01接收到的消息:"+message+"此消息被拒绝");
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            }else {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
            System.out.println("Consumer01接收到消息:" + message);
        };
        channel.basicConsume(NORMAL_QUEUE,false, deliverCallback, consumerTag ->{});
    }
}
