package com.qiubithub.kafka.controller;

import com.qiubithub.kafka.model.Notification;
import com.qiubithub.kafka.model.Order;
import com.qiubithub.kafka.model.User;
import com.qiubithub.kafka.service.KafkaAdvancedFeatureService;
import com.qiubithub.kafka.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaController {

    @Value("${app.kafka.topics.order-topic}")
    private String orderTopic;

    @Value("${app.kafka.topics.user-topic}")
    private String userTopic;

    @Value("${app.kafka.topics.notification-topic}")
    private String notificationTopic;

    private final KafkaProducerService producerService;
    private final KafkaAdvancedFeatureService advancedFeatureService;

    @Autowired
    public KafkaController(KafkaProducerService producerService, KafkaAdvancedFeatureService advancedFeatureService) {
        this.producerService = producerService;
        this.advancedFeatureService = advancedFeatureService;
    }

    /**
     * 发送订单消息
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> sendOrder(@RequestBody(required = false) Order order) {
        if (order == null) {
            order = createSampleOrder();
        }
        
        producerService.sendOrder(order);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderId", order.getOrderId());
        response.put("message", "订单消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 发送用户消息
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> sendUser(@RequestBody(required = false) User user) {
        if (user == null) {
            user = createSampleUser();
        }
        
        producerService.sendUser(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", user.getUserId());
        response.put("message", "用户消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 发送通知消息
     */
    @PostMapping("/notifications")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody(required = false) Notification notification) {
        if (notification == null) {
            notification = createSampleNotification();
        }
        
        producerService.sendNotification(notification);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("notificationId", notification.getNotificationId());
        response.put("message", "通知消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量生成订单消息
     */
    @PostMapping("/orders/bulk/{count}")
    public ResponseEntity<Map<String, Object>> generateBulkOrders(@PathVariable int count) {
        // 异步执行，避免请求超时
        CompletableFuture.runAsync(() -> {
            producerService.sendBulkOrders(count);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "开始生成 " + count + " 条订单消息");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 发送带头部的消息
     */
    @PostMapping("/headers")
    public ResponseEntity<Map<String, Object>> sendWithHeaders(
            @RequestParam(defaultValue = "test-key") String key,
            @RequestParam(defaultValue = "test-message") String message,
            @RequestParam(defaultValue = "order-topic") String topic) {
        
        advancedFeatureService.sendMessageWithHeaders(key, message, topic);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("key", key);
        response.put("message", "带头部的消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 事务性发送消息
     */
    @PostMapping("/transaction")
    public ResponseEntity<Map<String, Object>> sendInTransaction(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "order-topic") String topic) {
        
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            messages.add("Transaction message " + i);
        }
        
        advancedFeatureService.sendMessagesInTransaction(messages, topic);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        response.put("message", "事务性消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量发送消息
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> sendBatch(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "order-topic") String topic) {
        
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            messages.add("Batch message " + i);
        }
        
        advancedFeatureService.sendBatchMessages(messages, topic);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        response.put("message", "批量消息已发送");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 创建示例订单
     */
    private Order createSampleOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId("user-" + (int)(Math.random() * 1000));
        order.setAmount(new BigDecimal(String.format("%.2f", Math.random() * 1000)));
        order.setCreateTime(LocalDateTime.now());
        order.setStatus("CREATED");
        order.setProductId("product-" + (int)(Math.random() * 100));
        order.setQuantity(1 + (int)(Math.random() * 5));
        return order;
    }

    /**
     * 创建示例用户
     */
    private User createSampleUser() {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername("user" + (int)(Math.random() * 1000));
        user.setEmail(user.getUsername() + "@example.com");
        user.setRegisterTime(LocalDateTime.now());
        user.setStatus("ACTIVE");
        return user;
    }

    /**
     * 创建示例通知
     */
    private Notification createSampleNotification() {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId("user-" + (int)(Math.random() * 1000));
        notification.setTitle("通知标题 " + (int)(Math.random() * 100));
        notification.setContent("这是一条测试通知内容，请查收！");
        notification.setCreateTime(LocalDateTime.now());
        notification.setType("SYSTEM");
        notification.setRead(false);
        return notification;
    }
}