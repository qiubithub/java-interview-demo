package com.qiubithub.kafka.controller;

import com.qiubithub.kafka.model.Notification;
import com.qiubithub.kafka.model.Order;
import com.qiubithub.kafka.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kafka模拟控制器，不依赖于Kafka，可在Kafka不可用时使用
 */
@Slf4j
@RestController
@RequestMapping("/api/kafka-simulation")
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class KafkaSimulationController {

    // 模拟消息队列
    private final Map<String, BlockingQueue<Object>> topics = new HashMap<>();
    
    // 消费者线程池
    private final ExecutorService consumerExecutor = Executors.newCachedThreadPool();
    
    // 统计信息
    private final Map<String, AtomicInteger> messageCount = new HashMap<>();
    private final Map<String, AtomicInteger> consumedCount = new HashMap<>();
    
    // 是否正在消费
    private volatile boolean consuming = false;
    
    // 默认主题
    private static final String ORDER_TOPIC = "order-topic";
    private static final String USER_TOPIC = "user-topic";
    private static final String NOTIFICATION_TOPIC = "notification-topic";
    
    // 构造函数，初始化队列
    public KafkaSimulationController() {
        // 初始化主题队列
        topics.put(ORDER_TOPIC, new LinkedBlockingQueue<>());
        topics.put(USER_TOPIC, new LinkedBlockingQueue<>());
        topics.put(NOTIFICATION_TOPIC, new LinkedBlockingQueue<>());
        
        // 初始化计数器
        messageCount.put(ORDER_TOPIC, new AtomicInteger(0));
        messageCount.put(USER_TOPIC, new AtomicInteger(0));
        messageCount.put(NOTIFICATION_TOPIC, new AtomicInteger(0));
        
        consumedCount.put(ORDER_TOPIC, new AtomicInteger(0));
        consumedCount.put(USER_TOPIC, new AtomicInteger(0));
        consumedCount.put(NOTIFICATION_TOPIC, new AtomicInteger(0));
        
        log.info("Kafka模拟器已初始化，可以使用模拟功能");
    }

    /**
     * 发送订单消息
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> sendOrder(@RequestBody(required = false) Order order) {
        if (order == null) {
            order = createSampleOrder();
        }
        
        boolean added = topics.get(ORDER_TOPIC).offer(order);
        if (added) {
            messageCount.get(ORDER_TOPIC).incrementAndGet();
            log.info("模拟发送订单消息: {}", order.getOrderId());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("orderId", order.getOrderId());
        response.put("message", added ? "订单消息已发送" : "发送失败，队列已满");
        
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
        
        boolean added = topics.get(USER_TOPIC).offer(user);
        if (added) {
            messageCount.get(USER_TOPIC).incrementAndGet();
            log.info("模拟发送用户消息: {}", user.getUserId());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("userId", user.getUserId());
        response.put("message", added ? "用户消息已发送" : "发送失败，队列已满");
        
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
        
        boolean added = topics.get(NOTIFICATION_TOPIC).offer(notification);
        if (added) {
            messageCount.get(NOTIFICATION_TOPIC).incrementAndGet();
            log.info("模拟发送通知消息: {}", notification.getNotificationId());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", added);
        response.put("notificationId", notification.getNotificationId());
        response.put("message", added ? "通知消息已发送" : "发送失败，队列已满");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量生成订单消息
     */
    @PostMapping("/orders/bulk/{count}")
    public ResponseEntity<Map<String, Object>> generateBulkOrders(@PathVariable int count) {
        CompletableFuture.runAsync(() -> {
            log.info("开始批量生成{}条订单消息", count);
            int successCount = 0;
            for (int i = 0; i < count; i++) {
                Order order = createSampleOrder();
                boolean added = topics.get(ORDER_TOPIC).offer(order);
                if (added) {
                    messageCount.get(ORDER_TOPIC).incrementAndGet();
                    successCount++;
                }
                
                // 避免过快生成
                if (i % 100 == 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("完成批量生成订单消息，成功: {}/{}", successCount, count);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "开始生成 " + count + " 条订单消息");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 启动消费
     */
    @PostMapping("/consume/start")
    public ResponseEntity<Map<String, Object>> startConsuming(
            @RequestParam(defaultValue = "3") int consumerCount,
            @RequestParam(defaultValue = "100") int consumeSpeed) {
        
        if (consuming) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消费者已经在运行中");
            return ResponseEntity.ok(response);
        }
        
        consuming = true;
        
        // 启动订单消费者
        for (int i = 0; i < consumerCount; i++) {
            startConsumer(ORDER_TOPIC, "订单消费者-" + i, consumeSpeed);
        }
        
        // 启动用户消费者
        for (int i = 0; i < consumerCount; i++) {
            startConsumer(USER_TOPIC, "用户消费者-" + i, consumeSpeed);
        }
        
        // 启动通知消费者
        for (int i = 0; i < consumerCount; i++) {
            startConsumer(NOTIFICATION_TOPIC, "通知消费者-" + i, consumeSpeed);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "启动了 " + (consumerCount * 3) + " 个消费者，消费速度: " + consumeSpeed + "ms/消息");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 停止消费
     */
    @PostMapping("/consume/stop")
    public ResponseEntity<Map<String, Object>> stopConsuming() {
        consuming = false;
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已停止所有消费者");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        Map<String, Object> orderStatus = new HashMap<>();
        orderStatus.put("queueSize", topics.get(ORDER_TOPIC).size());
        orderStatus.put("totalProduced", messageCount.get(ORDER_TOPIC).get());
        orderStatus.put("totalConsumed", consumedCount.get(ORDER_TOPIC).get());
        
        Map<String, Object> userStatus = new HashMap<>();
        userStatus.put("queueSize", topics.get(USER_TOPIC).size());
        userStatus.put("totalProduced", messageCount.get(USER_TOPIC).get());
        userStatus.put("totalConsumed", consumedCount.get(USER_TOPIC).get());
        
        Map<String, Object> notificationStatus = new HashMap<>();
        notificationStatus.put("queueSize", topics.get(NOTIFICATION_TOPIC).size());
        notificationStatus.put("totalProduced", messageCount.get(NOTIFICATION_TOPIC).get());
        notificationStatus.put("totalConsumed", consumedCount.get(NOTIFICATION_TOPIC).get());
        
        status.put("orderTopic", orderStatus);
        status.put("userTopic", userStatus);
        status.put("notificationTopic", notificationStatus);
        status.put("consuming", consuming);
        
        return ResponseEntity.ok(status);
    }

    /**
     * 启动一个消费者线程
     */
    private void startConsumer(String topic, String consumerName, int consumeSpeed) {
        consumerExecutor.submit(() -> {
            log.info("{}启动", consumerName);
            BlockingQueue<Object> queue = topics.get(topic);
            AtomicInteger counter = consumedCount.get(topic);
            
            while (consuming && !Thread.currentThread().isInterrupted()) {
                try {
                    Object message = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (message != null) {
                        // 模拟消息处理
                        String messageId = getMessageId(message);
                        log.info("{} 处理消息: {}", consumerName, messageId);
                        Thread.sleep(consumeSpeed);
                        counter.incrementAndGet();
                        log.info("{} 处理完成: {}", consumerName, messageId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("{} 处理消息异常", consumerName, e);
                }
            }
            
            log.info("{}已停止", consumerName);
        });
    }

    /**
     * 获取消息ID
     */
    private String getMessageId(Object message) {
        if (message instanceof Order) {
            return ((Order) message).getOrderId();
        } else if (message instanceof User) {
            return ((User) message).getUserId();
        } else if (message instanceof Notification) {
            return ((Notification) message).getNotificationId();
        }
        return message.toString();
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