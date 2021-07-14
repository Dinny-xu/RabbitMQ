package com.study.rabbitmq.utils;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqUtils {

    public static Channel getChannel() throws Exception {

        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("42.194.222.147");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
}
