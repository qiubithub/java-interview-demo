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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息堆积处理器
 * 实现了多种消息堆积处理策略
 */
@Slf4j
@Service
@com.qiubithub.mq.config.MQCondition
public class MessageAccumulationHandler {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Autowired
    private TemporaryQueueService temporaryQueueService;

    private DefaultMQPushConsumer accumulationConsumer;
    private ExecutorService monitorExecutor;
    private final AtomicBoolean accumulationDetected = new AtomicBoolean(false);
    private final AtomicLong totalReceived = new AtomicLong(0);
    private final AtomicLong totalProcessed = new AtomicLong(0);
    
    // 堆积阈值
    private static final int ACCUMULATION_THRESHOLD = 1000;
    
    // 处理策略类型
    public enum AccumulationStrategy {
        NORMAL,              // 正常处理
        TEMPORARY_QUEUE,     // 使用临时队列
        SKIP_NONESSENTIAL,   // 跳过非关键消息
        BATCH_PROCESSING     // 批量处理
    }
    
    // 当前使用的策略
    private AccumulationStrategy currentStrategy = AccumulationStrategy.NORMAL;

    @PostConstruct
    public void init() throws MQClientException {
        // 创建消费者实例
        accumulationConsumer = new DefaultMQPushConsumer(RocketMQConfig.ORDER_GROUP + "-accumulation-handler");
        accumulationConsumer.setNamesrvAddr(nameServer);
        accumulationConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        accumulationConsumer.setMessageModel(MessageModel.CLUSTERING);
        accumulationConsumer.setConsumeThreadMin(10);
        accumulationConsumer.setConsumeThreadMax(20);
        accumulationConsumer.setPullBatchSize(100);
        
        // 订阅主题
        accumulationConsumer.subscribe(RocketMQConfig.ORDER_TOPIC, "*");
        
        // 注册消息监听器
        accumulationConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                // 更新接收计数
                totalReceived.addAndGet(msgs.size());
                
                // 根据当前策略处理消息
                switch (currentStrategy) {
                    case NORMAL:
                        return processNormally(msgs);
                    case TEMPORARY_QUEUE:
                        return processWithTemporaryQueue(msgs);
                    case SKIP_NONESSENTIAL:
                        return processWithSkipping(msgs);
                    case BATCH_PROCESSING:
                        return processInBatch(msgs);
                    default:
                        return processNormally(msgs);
                }
            }
        });
        
        // 启动消费者
        accumulationConsumer.start();
        log.info("消息堆积处理器启动成功");
        
        // 创建监控线程
        monitorExecutor = Executors.newSingleThreadExecutor();
        monitorExecutor.submit(this::monitorAccumulation);
    }
    
    /**
     * 监控消息堆积情况
     */
    private void monitorAccumulation() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(5);
                
                // 获取堆积情况
                long accumulated = getAccumulatedMessageCount();
                long processed = totalProcessed.get();
                long received = totalReceived.get();
                
                log.info("消息监控 - 总接收: {}, 总处理: {}, 估计堆积: {}, 当前策略: {}", 
                        received, processed, accumulated, currentStrategy);
                
                // 检测堆积并切换策略
                if (accumulated > ACCUMULATION_THRESHOLD && !accumulationDetected.get()) {
                    log.warn("检测到消息堆积，切换处理策略");
                    accumulationDetected.set(true);
                    switchToAccumulationStrategy(AccumulationStrategy.TEMPORARY_QUEUE);
                } else if (accumulated < ACCUMULATION_THRESHOLD / 2 && accumulationDetected.get()) {
                    log.info("消息堆积已缓解，恢复正常处理");
                    accumulationDetected.set(false);
                    switchToAccumulationStrategy(AccumulationStrategy.NORMAL);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("监控消息堆积异常", e);
            }
        }
    }
    
    /**
     * 切换消息堆积处理策略
     */
    public void switchToAccumulationStrategy(AccumulationStrategy strategy) {
        log.info("切换消息堆积处理策略: {} -> {}", currentStrategy, strategy);
        
        // 如果从临时队列策略切换出去，停止临时队列处理
        if (currentStrategy == AccumulationStrategy.TEMPORARY_QUEUE && strategy != AccumulationStrategy.TEMPORARY_QUEUE) {
            temporaryQueueService.stopProcessing();
        }
        
        // 如果切换到临时队列策略，启动临时队列处理
        if (strategy == AccumulationStrategy.TEMPORARY_QUEUE && currentStrategy != AccumulationStrategy.TEMPORARY_QUEUE) {
            temporaryQueueService.startProcessing();
        }
        
        currentStrategy = strategy;
    }
    
    /**
     * 正常处理消息
     */
    private ConsumeConcurrentlyStatus processNormally(List<MessageExt> msgs) {
        for (MessageExt msg : msgs) {
            try {
                // 解析消息
                OrderMessage orderMessage = parseMessage(msg);
                if (orderMessage == null) continue;
                
                log.info("正常处理消息: {}", orderMessage.getOrderId());
                
                // 模拟处理时间
                TimeUnit.MILLISECONDS.sleep(200);
                
                // 更新处理计数
                totalProcessed.incrementAndGet();
                
                log.info("正常处理消息完成: {}", orderMessage.getOrderId());
            } catch (Exception e) {
                log.error("处理消息异常", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
    
    /**
     * 使用临时队列处理消息
     */
    private ConsumeConcurrentlyStatus processWithTemporaryQueue(List<MessageExt> msgs) {
        for (MessageExt msg : msgs) {
            try {
                // 解析消息
                OrderMessage orderMessage = parseMessage(msg);
                if (orderMessage == null) continue;
                
                // 添加到临时队列
                boolean added = temporaryQueueService.addToTemporaryQueue(orderMessage);
                if (added) {
                    // 更新处理计数
                    totalProcessed.incrementAndGet();
                    log.debug("消息已添加到临时队列: {}", orderMessage.getOrderId());
                } else {
                    // 临时队列已满，稍后重试
                    log.warn("临时队列已满，稍后重试: {}", orderMessage.getOrderId());
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            } catch (Exception e) {
                log.error("处理消息异常", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
    
    /**
     * 跳过非关键消息
     */
    private ConsumeConcurrentlyStatus processWithSkipping(List<MessageExt> msgs) {
        for (MessageExt msg : msgs) {
            try {
                // 解析消息
                OrderMessage orderMessage = parseMessage(msg);
                if (orderMessage == null) continue;
                
                // 判断是否为关键消息（这里简单根据标签判断）
                boolean isEssential = RocketMQConfig.ORDER_TAG_PRIORITY.equals(msg.getTags());
                
                if (isEssential) {
                    // 处理关键消息
                    log.info("处理关键消息: {}", orderMessage.getOrderId());
                    TimeUnit.MILLISECONDS.sleep(100);
                    log.info("处理关键消息完成: {}", orderMessage.getOrderId());
                } else {
                    // 跳过非关键消息
                    log.debug("跳过非关键消息: {}", orderMessage.getOrderId());
                }
                
                // 更新处理计数
                totalProcessed.incrementAndGet();
            } catch (Exception e) {
                log.error("处理消息异常", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
    
    /**
     * 批量处理消息
     */
    private ConsumeConcurrentlyStatus processInBatch(List<MessageExt> msgs) {
        if (msgs.isEmpty()) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        
        try {
            log.info("批量处理 {} 条消息", msgs.size());
            
            // 模拟批量处理，实际中可能是批量写入数据库等
            TimeUnit.MILLISECONDS.sleep(msgs.size() * 10);
            
            // 更新处理计数
            totalProcessed.addAndGet(msgs.size());
            
            log.info("批量处理 {} 条消息完成", msgs.size());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("批量处理消息异常", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    
    /**
     * 解析消息内容
     */
    private OrderMessage parseMessage(MessageExt msg) {
        try {
            String json = new String(msg.getBody(), StandardCharsets.UTF_8);
            // 实际项目中应该使用JSON库解析
            // 这里简单模拟一个OrderMessage对象
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setOrderId(msg.getMsgId());
            orderMessage.setStatus("CREATED");
            return orderMessage;
        } catch (Exception e) {
            log.error("解析消息异常", e);
            return null;
        }
    }
    
    /**
     * 获取堆积的消息数量
     * 实际项目中应该通过RocketMQ Admin API获取
     */
    private long getAccumulatedMessageCount() {
        // 这里使用收到的消息和处理的消息数量差值作为堆积量的估计
        return totalReceived.get() - totalProcessed.get();
    }
    
    /**
     * 获取当前策略
     */
    public AccumulationStrategy getCurrentStrategy() {
        return currentStrategy;
    }
    
    /**
     * 获取统计信息
     */
    public String getStatistics() {
        return String.format("接收: %d, 处理: %d, 堆积: %d, 策略: %s", 
                totalReceived.get(), totalProcessed.get(), 
                getAccumulatedMessageCount(), currentStrategy);
    }
    
    @PreDestroy
    public void destroy() {
        if (accumulationConsumer != null) {
            accumulationConsumer.shutdown();
        }
        if (monitorExecutor != null) {
            monitorExecutor.shutdownNow();
        }
        log.info("消息堆积处理器关闭");
    }
}