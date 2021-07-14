package com.study.rabbitmq.producer;

import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final static String QUEUE_NAME = "study";

    public static void main(String[] args) throws IOException, TimeoutException {

        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("42.194.222.147");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("admin");
        factory.setPassword("admin");
        //channel 实现了自动close接口 自动关闭 不需要显示关闭
        final Connection connection = factory.newConnection();
        //获取信道
        final Channel channel = connection.createChannel();

        //优先级队列测试 -> 添加参数:x-max-priority 设置优先级条数10
        HashMap<String, Object> params = MapUtil.newHashMap();
        params.put("x-max-priority", 10);
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder().priority(5).build();
        //生成队列-> 设置参数为优先级队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, params);
        for (int i = 1; i < 11; i++) {
            String msg = "info" + i;
            //如果是第五条消息, 将会被优先消费
            if (i == 5) {
                channel.basicPublish("",QUEUE_NAME,props,msg.getBytes(StandardCharsets.UTF_8));
            }else {
                channel.basicPublish("",QUEUE_NAME,null,msg.getBytes(StandardCharsets.UTF_8));
            }
        }
        /*
         * 生成一个队列(必须写没有的队列，已有队列会报错)
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "单独发一条消息";

        /*
         * 发送一个消息
         * 1.发送到那个交换机
         * 2.路由的 key 是哪个
         * 3.其他的参数信息
         * 4.发送消息的消息体
         */
        //channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }
}
