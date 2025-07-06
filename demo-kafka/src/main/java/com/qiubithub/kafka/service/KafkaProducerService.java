package com.qiubithub.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.kafka.model.Notification;
import com.qiubithub.kafka.model.Order;
import com.qiubithub.kafka.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaProducerService {

    @Value("${app.kafka.topics.order-topic}")
    private String orderTopic;

    @Value("${app.kafka.topics.user-topic}")
    private String userTopic;

    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 发送订单消息
     */
    public void sendOrder(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(orderTopic, order.getOrderId(), orderJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("发送订单消息成功: {} 到分区: {}", order.getOrderId(), result.getRecordMetadata().partition());
                } else {
                    log.error("发送订单消息失败: {}", order.getOrderId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("订单序列化失败", e);
        }
    }

    /**
     * 发送用户消息
     */
    public void sendUser(User user) {
        try {
            String userJson = objectMapper.writeValueAsString(user);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(userTopic, user.getUserId(), userJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("发送用户消息成功: {} 到分区: {}", user.getUserId(), result.getRecordMetadata().partition());
                } else {
                    log.error("发送用户消息失败: {}", user.getUserId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("用户序列化失败", e);
        }
    }

    /**
     * 发送通知消息
     */
    public void sendNotification(Notification notification) {
        try {
            String notificationJson = objectMapper.writeValueAsString(notification);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(notificationTopic, notification.getUserId(), notificationJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("发送通知消息成功: {} 到分区: {}", notification.getNotificationId(), result.getRecordMetadata().partition());
                } else {
                    log.error("发送通知消息失败: {}", notification.getNotificationId(), ex);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("通知序列化失败", e);
        }
    }

    /**
     * 批量发送订单消息
     */
    public void sendBulkOrders(int count) {
        log.info("开始批量发送{}条订单消息", count);
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setOrderId("BULK-" + System.currentTimeMillis() + "-" + i);
            order.setUserId("user-" + (i % 100));
            order.setStatus("CREATED");
            order.setProductId("product-" + (i % 20));
            order.setQuantity(1 + (i % 5));
            
            sendOrder(order);
            
            // 避免发送过快
            if (i % 100 == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.info("完成批量发送{}条订单消息", count);
    }
}