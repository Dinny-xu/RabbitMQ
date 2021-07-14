package com.study.rabbitmq.confirm;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {


    /*
     * 交换机不管是否收到一个消息的一个回调方法
     * CorrelationDate
     * 消息相关数据
     * ack
     * 交换机是否收到消息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData.getId() != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到id为:{}的消息", id);
        } else {
            log.info("交换机还未收到id为:{}消息，由于原因:{}", id, cause);
        }
    }


    /*
     * 在仅开启了生产者确认机制的情况下，交换机接收到消息后，会直接给消息生产者发送确认消息
     * 如果发现该消息不可路由，那么消息会被直接丢弃，此时生产者是不知道消息被丢弃这个事件的。
     * 那么如何让无法被路由的消息帮我想办法处理一下？
     * 通过设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息{},被交换机{}退回,退回原因:{},路由key:{}",new String(message.getBody()),exchange,replyText,routingKey);
    }
}
