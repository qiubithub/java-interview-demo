package com.qiubithub.mq.service;

import com.qiubithub.mq.config.RocketMQConfig;
import com.qiubithub.mq.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@com.qiubithub.mq.config.MQCondition
public class ScalableOrderConsumer {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.consumer.pull-batch-size:10}")
    private int pullBatchSize;

    private DefaultMQPushConsumer consumer;
    private ExecutorService executorService;
    private final AtomicBoolean isScalingUp = new AtomicBoolean(false);
    private final int maxThreads = 20; // 最大线程数
    private final int initialThreads = 5; // 初始线程数
    private int currentThreads = initialThreads;

    @PostConstruct
    public void init() throws MQClientException {
        // 创建消费者实例
        consumer = new DefaultMQPushConsumer(RocketMQConfig.ORDER_GROUP + "-scalable");
        consumer.setNamesrvAddr(nameServer);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeThreadMin(initialThreads);
        consumer.setConsumeThreadMax(maxThreads);
        consumer.setPullBatchSize(pullBatchSize);
        
        // 订阅主题
        consumer.subscribe(RocketMQConfig.ORDER_TOPIC, RocketMQConfig.ORDER_TAG_NORMAL);
        
        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    try {
                        // 检查消息堆积情况并自动扩容
                        checkAndScaleIfNeeded();
                        
                        // 处理消息
                        log.info("可扩展消费者接收到消息: {} Queue: {}", msg.getMsgId(), msg.getQueueId());
                        
                        // 模拟处理时间
                        TimeUnit.MILLISECONDS.sleep(100);
                        
                        log.info("可扩展消费者处理消息完成: {}", msg.getMsgId());
                    } catch (Exception e) {
                        log.error("处理消息异常", e);
                        // 稍后重试
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        
        // 启动消费者
        consumer.start();
        log.info("可扩展消费者启动成功，初始线程数: {}", initialThreads);
        
        // 创建监控线程池
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::monitorMessageAccumulation);
    }
    
    /**
     * 监控消息堆积情况
     */
    private void monitorMessageAccumulation() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(5);
                checkAndScaleIfNeeded();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("监控消息堆积异常", e);
            }
        }
    }
    
    /**
     * 检查并根据需要扩容或缩容
     */
    private void checkAndScaleIfNeeded() {
        try {
            // 这里可以通过RocketMQ Admin API获取实际的消息堆积量
            // 为了演示，我们模拟一个堆积检测逻辑
            long accumulatedMsgCount = getAccumulatedMessageCount();
            log.info("当前消息堆积量: {}, 当前消费线程数: {}", accumulatedMsgCount, currentThreads);
            
            // 如果堆积超过阈值，增加消费线程
            if (accumulatedMsgCount > 1000 && currentThreads < maxThreads) {
                scaleUp();
            } 
            // 如果堆积量较少，减少消费线程
            else if (accumulatedMsgCount < 100 && currentThreads > initialThreads) {
                scaleDown();
            }
        } catch (Exception e) {
            log.error("检查并扩容异常", e);
        }
    }
    
    /**
     * 扩容消费线程
     */
    private void scaleUp() {
        if (isScalingUp.compareAndSet(false, true)) {
            try {
                int newThreads = Math.min(currentThreads + 5, maxThreads);
                log.info("扩容消费线程: {} -> {}", currentThreads, newThreads);
                consumer.setConsumeThreadMin(newThreads);
                currentThreads = newThreads;
            } finally {
                isScalingUp.set(false);
            }
        }
    }
    
    /**
     * 缩容消费线程
     */
    private void scaleDown() {
        if (isScalingUp.compareAndSet(false, true)) {
            try {
                int newThreads = Math.max(currentThreads - 2, initialThreads);
                log.info("缩容消费线程: {} -> {}", currentThreads, newThreads);
                consumer.setConsumeThreadMin(newThreads);
                currentThreads = newThreads;
            } finally {
                isScalingUp.set(false);
            }
        }
    }
    
    /**
     * 获取堆积的消息数量
     * 实际项目中应该通过RocketMQ Admin API获取
     */
    private long getAccumulatedMessageCount() {
        // 这里模拟一个随机数，实际应该通过RocketMQ Admin API获取
        return (long) (Math.random() * 2000);
    }
    
    @PreDestroy
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log.info("可扩展消费者关闭");
    }
}