package com.study.rabbitmq.exchange;


import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 主题交换机 Topic exchange
 */
public class EmitLogTopic {

    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {

        val channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        /*
         * Q1: 绑定中间带有orange的3个单词字符串(*.orange.*)
         * Q2: 绑定最后一个单词是rabbit的3个单词(*.*.rabbit)
         *     第一个绑定单词是lazy的多个单词(lazy.#)
         */
        HashMap<String, String> bindingKeyMap = MapUtil.newHashMap();
        bindingKeyMap.put("quick.orange.rabbit", "被队列Q1-Q2接收");
        bindingKeyMap.put("lazy.orange.elephant", "被队列Q1-Q2接收");
        bindingKeyMap.put("quick.orange.fox", "被队列Q1接收");
        bindingKeyMap.put("lazy.brown.fox", "被队列Q2接收");
        bindingKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定条件，但只会被队列Q2接收一次");
        bindingKeyMap.put("quick.brown.fox", "不匹配任何绑定，不会被任何队列接收-会被丢弃");
        bindingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定-会被丢弃");
        bindingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配Q2");

        for (Map.Entry<String, String> bindingKeyEntry : bindingKeyMap.entrySet()) {
            val bindingKey = bindingKeyEntry.getKey();
            val message = bindingKeyEntry.getValue();
            channel.basicPublish(EXCHANGE_NAME, bindingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息:" + message);
        }
    }

}
