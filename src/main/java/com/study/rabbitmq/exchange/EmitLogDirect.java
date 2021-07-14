package com.study.rabbitmq.exchange;

import cn.hutool.core.map.MapUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * 直接交换机 Direct exchange -> 指定键进行消费
 */
public class EmitLogDirect {
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        val channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //创建多个bindingKey
        HashMap<String, String> bindingKeyMap = MapUtil.newHashMap();
        bindingKeyMap.put("info", "普通info消息");
        bindingKeyMap.put("warning", "普通warning消息");
        bindingKeyMap.put("error", "错误error消息");

        //由于没有debug 这个键进行指定消费，所以debug这条消息并未被消费
        bindingKeyMap.put("debug", "调试debug信息");
        for (Map.Entry<String, String> bindingKeyEntry : bindingKeyMap.entrySet()) {
            val bindingKey = bindingKeyEntry.getKey();
            val message = bindingKeyEntry.getValue();
            channel.basicPublish(EXCHANGE_NAME, bindingKey, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
