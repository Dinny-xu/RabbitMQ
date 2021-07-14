package com.study.rabbitmq.confirm;

import cn.hutool.core.util.IdUtil;
import com.rabbitmq.client.ConfirmCallback;
import com.study.rabbitmq.utils.RabbitMqUtils;
import lombok.val;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1：单个发布确认
 * 2：批量确认
 * 3：异步确认
 */
public class ConfirmMessage {

    public static final int MESSAGE_COUNT = 1000;


    public static void main(String[] args) throws Exception {
        //发布1000个单独确认消息，耗时:45662ms
//        publishMessageIndividually();
//        发布1000条批量确认消息，耗时:525ms
//        publishMessageBatch();
        publishMessageAsync();//发布1000个异步确认消息，耗时16ms
    }


    /**
     * 单个发布确认
     *
     * @throws Exception
     */
    public static void publishMessageIndividually() throws Exception {
        val channel = RabbitMqUtils.getChannel();
        val queueName = IdUtil.fastSimpleUUID();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        val begin = System.currentTimeMillis();

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //服务端返回false或超时时间内未返回，生产者可以重发
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        val end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时:" + (end - begin) + "ms");
    }


    /**
     * 批量发布确认
     *
     * @throws Exception
     */
    public static void publishMessageBatch() throws Exception {
        val channel = RabbitMqUtils.getChannel();
        val queueName = IdUtil.fastSimpleUUID();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        val begin = System.currentTimeMillis();

        int batchSize = 100;

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());

            if (i % batchSize == 0) {
                channel.waitForConfirms();
            }
        }
        val end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "条批量确认消息，耗时:" + (end - begin) + "ms");
    }


    /**
     * 异步确认消息
     *
     * @throws Exception
     */
    public static void publishMessageAsync() throws Exception {
        val channel = RabbitMqUtils.getChannel();
        val queueName = IdUtil.fastSimpleUUID();
        channel.queueDeclare(queueName, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        /*
         * 线程安全有序的一个哈希表，适用于高并发情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号就可以
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> map = new ConcurrentSkipListMap<>();

        /*
         * 确认收到消息的一个回调
         * 1.消息序列号
         * 2.true 可以确认小于当前序列号的消息
         *   false 确认当前序列号消息
         */
        ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息，是一个map
                ConcurrentNavigableMap<Long, String> confirmed = map.headMap(sequenceNumber, true);
                //清除该部分未确认消息
                //confirmed.clear();
            }
   /*         else {
                //只清除当前序列号的消息
                map.remove(sequenceNumber);
            }*/
            System.out.println("确认的消息：" + sequenceNumber);
        };
        ConfirmCallback nackCallback = (sequenceNumber, multiple) -> {
            String message = map.get(sequenceNumber);
            System.out.println("发布的消息" + message + "未被确认，序列号" + sequenceNumber);
        };
        /*
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        val begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息：" + i;
            /*
             * channel.getNextPublishSeqNo() 获取下一个消息的序列号
             * 通过序列号与消息进行一个关联
             * 全部都是未确认的消息体
             */
            map.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        val end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息，耗时" + (end - begin) + "ms");
    }
}
