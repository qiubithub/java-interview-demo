package com.qiubithub.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 演示Kafka高级特性
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaAdvancedFeatureService {

    @Value("${app.kafka.topics.order-topic}")
    private String orderTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaAdvancedFeatureService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送带有自定义头部的消息
     */
    public void sendMessageWithHeaders(String key, String value, String topicName) {
        // 创建头部
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("app-id", "demo-kafka".getBytes(StandardCharsets.UTF_8)));
        headers.add(new RecordHeader("trace-id", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
        headers.add(new RecordHeader("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)));

        // 创建生产者记录，包含头部信息
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, null, key, value, headers);

        // 发送消息
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("发送带头部的消息成功: {} 到分区: {}", key, result.getRecordMetadata().partition());
            } else {
                log.error("发送带头部的消息失败: {}", key, ex);
            }
        });
    }

    /**
     * 事务性发送消息
     */
    public void sendMessagesInTransaction(List<String> messages, String topicName) {
        kafkaTemplate.executeInTransaction(operations -> {
            try {
                for (String message : messages) {
                    String key = UUID.randomUUID().toString();
                    CompletableFuture<SendResult<String, String>> future = operations.send(topicName, key, message);
                    future.get(10, TimeUnit.SECONDS); // 等待发送结果
                    log.info("在事务中发送消息: {}", key);
                }
                return true;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("事务性发送消息失败", e);
                throw new RuntimeException("事务性发送消息失败", e);
            }
        });
        log.info("事务性发送完成，共发送{}条消息", messages.size());
    }

    /**
     * 消费带有头部的消息
     */
    @KafkaListener(topics = "${app.kafka.topics.order-topic}", groupId = "header-consumer-group")
    public void consumeWithHeaders(ConsumerRecord<String, String> record) {
        // 获取头部信息
        String appId = new String(record.headers().lastHeader("app-id").value(), StandardCharsets.UTF_8);
        String traceId = new String(record.headers().lastHeader("trace-id").value(), StandardCharsets.UTF_8);
        String timestamp = new String(record.headers().lastHeader("timestamp").value(), StandardCharsets.UTF_8);
        
        log.info("消费带头部的消息: key={}, value={}, appId={}, traceId={}, timestamp={}",
                record.key(), record.value(), appId, traceId, timestamp);
    }

    /**
     * 批量发送消息，演示批处理特性
     */
    public void sendBatchMessages(List<String> messages, String topicName) {
        List<CompletableFuture<SendResult<String, String>>> futures = new ArrayList<>();
        
        for (String message : messages) {
            String key = UUID.randomUUID().toString();
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, key, message);
            futures.add(future);
        }
        
        // 等待所有消息发送完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("批量发送成功，共{}条消息", messages.size());
                } else {
                    log.error("批量发送部分失败", ex);
                }
            });
    }
}