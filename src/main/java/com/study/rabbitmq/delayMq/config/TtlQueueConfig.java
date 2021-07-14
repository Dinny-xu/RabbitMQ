package com.study.rabbitmq.delayMq.config;

import cn.hutool.core.map.MapUtil;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


/**
 * 设置延迟队列
 * 延时队列,队列内部是有序的，最重要的特性就体现在它的延时属性上，延时队列中的元素是希望
 * 在指定时间到了以后或之前取出和处理，简单来说，延时队列就是用来存放需要在指定时间被处理的
 * 元素的队列。
 */
@Configuration
public class TtlQueueConfig {

    //声明一个x普通交换机和一个 Y死信延迟交换机
    public static final String X_EXCHANGE = "X";
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";

    //声明队列QA-QB 和 死信延迟队列QD
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    public static final String DEAD_LETTER_QUEUE = "QD";

    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }


    /**
     * 声明队列A TTL 为10s 并绑定到对应的死信交换机
     */
    @Bean("queueA")
    public Queue queueA() {
        HashMap<String, Object> args = MapUtil.newHashMap(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的TTL
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }


    /**
     * 声明队列QA绑定X交换机
     */
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }


    /**
     * 声明队列B TTL 为40s 并绑定到对应的死信交换机
     */
    @Bean("queueB")
    public Queue queueB() {
        HashMap<String, Object> args = MapUtil.newHashMap(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "YD");
        //声明队列的TTL
        args.put("x-message-ttl", 30000);
        return QueueBuilder.durable(QUEUE_B).withArguments(args).build();
    }


    /**
     * 声明队列QB绑定X交换机
     */
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queue1B, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queue1B).to(xExchange).with("XB");
    }


    /**
     * 声明队列QC 绑定死信交换机，不设置TTL 超时，由消息生产者设置TTL 时长，超过设置时长，消息被绑定YD的队列接收并消费
     */
    @Bean("queueC")
    public Queue queueC() {
        HashMap<String, Object> args = MapUtil.newHashMap(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(args).build();
    }

    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }


    /**
     * 声明死信队列QD
     */
    @Bean("queueD")
    public Queue queueD() {
        return new Queue(DEAD_LETTER_QUEUE);
    }


    /**
     * 声明死信队列QD绑定关系
     */
    @Bean
    public Binding deadLetterBindingQAD(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
