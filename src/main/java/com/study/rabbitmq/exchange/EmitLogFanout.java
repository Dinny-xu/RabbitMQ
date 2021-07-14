package com.study.rabbitmq.exchange;

import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * 扇形交换机 Fanout -> 发送消息，绑定的队列均可收到
 */
public class EmitLogFanout {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {

        val channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息" + message);
        }
    }
}
