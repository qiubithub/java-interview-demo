package com.qiubithub.mq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.consumer.pull-batch-size:10}")
    private int pullBatchSize;

    // 定义主题常量
    public static final String ORDER_TOPIC = "order-topic";
    public static final String ORDER_GROUP = "order-consumer-group";
    
    // 定义标签常量
    public static final String ORDER_TAG_NORMAL = "normal";
    public static final String ORDER_TAG_PRIORITY = "priority";
}