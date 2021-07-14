package com.study.rabbitmq.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig2 {

/*
    @Bean
    public Object secondQueue() {
        System.out.println("configuration secondQueue ........................");
        return new Queue("second", true);
    }

    @Bean
    DirectExchange secondExchange() {
        return new DirectExchange("second" );
    }

    @Bean
    Binding bindingSecondDirect() {
        return BindingBuilder.bind((Exchange) secondQueue()).to(secondExchange()).with("secondDirectRouting");

    }
*/


}
