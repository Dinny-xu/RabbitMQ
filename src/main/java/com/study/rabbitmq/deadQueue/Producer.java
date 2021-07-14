package com.study.rabbitmq.deadQueue;


import com.rabbitmq.client.BuiltinExchangeType;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.nio.charset.StandardCharsets;

/*
 * 发消息给Consumer01-> normal_exchange 普通交换机-> 绑定死信队列
 * 死信队列来源:
 *      1:消息过期
 *           Consumer01如果没有在TTL有效时间内消费 -> 消息将转入死信队列
 *      2:队列达到最大长度(队列满了，无法再添加数据到mq中)
 *          -> 关闭TTL，设置有效消息条数和死信消息条数-> Consumer01设置  params.put("x-max-length", 6);表明有6条消息进入正常队列，其余消息进入死信
 *      3:消息被拒绝(basic.reject或basic.nack)并且 requeue = false
 *          根据DeliverCallback 回调的消息判断需要拒绝的消息
 */
public class Producer {

    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //设置消息的TTL 时间
        //AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        //该信息是用于演示队列个数限制
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发送消息:" + message);
        }
    }
}
