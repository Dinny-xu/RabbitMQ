package com.study.rabbitmq.confirm.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布确认高级-配置类
 */
@Configuration
public class ConfirmConfig {

    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";

    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    //声明一个备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    //声明一个备份队列
    public static final String BACKUP_QUEUE_NAME = "backup.queue";

    public static final String WARNING_QUEUE_NAME = "warning.queue";


    /*
     * 声明业务交换机
     */
    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        ExchangeBuilder exchangeBuilder = ExchangeBuilder
                .directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true).withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME);
        return exchangeBuilder.build();
    }


    /*
     * 声明确认队列
     */
    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    /*
     * 声明确认队列绑定关系
     */
    @Bean
    public Binding queueBinding(@Qualifier("confirmExchange") DirectExchange exchange,
                                @Qualifier("confirmQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("key1");
    }

    /*
     * 声明备份交换机类型-> 扇形
     */
    @Bean("backupExchange")
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    /*
     * 声明备份队列
     */
    @Bean("backupQueue")
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    /*
     * 声明警告队列
     */
    @Bean("warningQueue")
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    /*
     * 声明报警队列绑定关系
     */
    @Bean
    public Binding warningBinding(@Qualifier("warningQueue") Queue queue, @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(queue).to(backupExchange);
    }

    @Bean
    public Binding backupBinding(@Qualifier("backupQueue") Queue queue, @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(queue).to(backupExchange);
    }

}
