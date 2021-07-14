package com.study.rabbitmq.config;


import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {

/*    @Bean
    public Queue firstQueue() {
        System.out.println("configuration firstQueue ........................");
        return new Queue("first", true);
    }


    @Bean
    DirectExchange firstExchange() {
        return new DirectExchange("first", true, false);
    }

    @Bean
    Binding bindingFirstDirect() {
        return BindingBuilder.bind(firstQueue()).to(firstExchange()).with("firstDirectRouting");
    }*/


}
