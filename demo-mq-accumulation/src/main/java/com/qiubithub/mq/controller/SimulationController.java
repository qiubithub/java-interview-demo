package com.qiubithub.mq.controller;

import com.qiubithub.mq.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模拟消息堆积的控制器
 * 不依赖于RocketMQ，可以在RocketMQ不可用时使用
 */
@Slf4j
@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    // 模拟消息队列
    private final BlockingQueue<OrderMessage> messageQueue = new LinkedBlockingQueue<>();
    
    // 模拟消费者线程池
    private ExecutorService consumerExecutor;
    
    // 消费者数量
    private int consumerCount = 2;
    
    // 消费速度（毫秒/消息）
    private int consumeSpeed = 200;
    
    // 是否正在消费
    private volatile boolean consuming = false;
    
    // 统计信息
    private final AtomicInteger totalProduced = new AtomicInteger(0);
    private final AtomicInteger totalConsumed = new AtomicInteger(0);
    private final AtomicInteger activeConsumers = new AtomicInteger(0);

    /**
     * 生成模拟消息
     */
    @PostMapping("/produce/{count}")
    public ResponseEntity<Map<String, Object>> produceMessages(@PathVariable int count) {
        CompletableFuture.runAsync(() -> {
            log.info("开始生成 {} 条模拟消息", count);
            for (int i = 0; i < count; i++) {
                OrderMessage message = createSampleOrderMessage();
                messageQueue.offer(message);
                totalProduced.incrementAndGet();
                
                if (i % 100 == 0) {
                    log.info("已生成 {} 条模拟消息", i);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("完成生成 {} 条模拟消息，当前队列大小: {}", count, messageQueue.size());
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "开始生成 " + count + " 条模拟消息");
        response.put("queueSize", messageQueue.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 启动消费
     */
    @PostMapping("/consume/start")
    public ResponseEntity<Map<String, Object>> startConsuming(
            @RequestParam(defaultValue = "2") int consumerCount,
            @RequestParam(defaultValue = "200") int consumeSpeed) {
        
        if (consuming) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消费者已经在运行中");
            return ResponseEntity.ok(response);
        }
        
        this.consumerCount = consumerCount;
        this.consumeSpeed = consumeSpeed;
        this.consuming = true;
        
        // 创建消费者线程池
        consumerExecutor = Executors.newFixedThreadPool(consumerCount);
        
        // 启动消费者
        for (int i = 0; i < consumerCount; i++) {
            final int consumerId = i;
            consumerExecutor.submit(() -> {
                activeConsumers.incrementAndGet();
                log.info("消费者 {} 启动，消费速度: {}ms/消息", consumerId, consumeSpeed);
                
                while (consuming && !Thread.currentThread().isInterrupted()) {
                    try {
                        OrderMessage message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (message != null) {
                            // 模拟消息处理
                            log.info("消费者 {} 处理消息: {}", consumerId, message.getOrderId());
                            TimeUnit.MILLISECONDS.sleep(consumeSpeed);
                            totalConsumed.incrementAndGet();
                            log.info("消费者 {} 处理完成: {}", consumerId, message.getOrderId());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("消费者 {} 处理消息异常", consumerId, e);
                    }
                }
                
                activeConsumers.decrementAndGet();
                log.info("消费者 {} 已停止", consumerId);
            });
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "启动 " + consumerCount + " 个消费者，消费速度: " + consumeSpeed + "ms/消息");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 停止消费
     */
    @PostMapping("/consume/stop")
    public ResponseEntity<Map<String, Object>> stopConsuming() {
        if (!consuming) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消费者未运行");
            return ResponseEntity.ok(response);
        }
        
        consuming = false;
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
            try {
                consumerExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已停止所有消费者");
        response.put("status", getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("queueSize", messageQueue.size());
        status.put("totalProduced", totalProduced.get());
        status.put("totalConsumed", totalConsumed.get());
        status.put("activeConsumers", activeConsumers.get());
        status.put("consuming", consuming);
        status.put("consumerCount", consumerCount);
        status.put("consumeSpeed", consumeSpeed);
        status.put("accumulationRate", calculateAccumulationRate());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 清空队列
     */
    @PostMapping("/queue/clear")
    public ResponseEntity<Map<String, Object>> clearQueue() {
        int size = messageQueue.size();
        messageQueue.clear();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已清空队列，原队列大小: " + size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 调整消费速度
     */
    @PostMapping("/consume/speed/{speed}")
    public ResponseEntity<Map<String, Object>> adjustConsumeSpeed(@PathVariable int speed) {
        this.consumeSpeed = speed;
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已调整消费速度为: " + speed + "ms/消息");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 调整消费者数量
     */
    @PostMapping("/consume/scale/{count}")
    public ResponseEntity<Map<String, Object>> scaleConsumers(@PathVariable int count) {
        if (!consuming) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "消费者未运行，请先启动消费");
            return ResponseEntity.ok(response);
        }
        
        int oldCount = this.consumerCount;
        this.consumerCount = count;
        
        // 如果增加消费者
        if (count > oldCount) {
            for (int i = oldCount; i < count; i++) {
                final int consumerId = i;
                consumerExecutor.submit(() -> {
                    activeConsumers.incrementAndGet();
                    log.info("新增消费者 {} 启动，消费速度: {}ms/消息", consumerId, consumeSpeed);
                    
                    while (consuming && !Thread.currentThread().isInterrupted()) {
                        try {
                            OrderMessage message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                            if (message != null) {
                                // 模拟消息处理
                                log.info("消费者 {} 处理消息: {}", consumerId, message.getOrderId());
                                TimeUnit.MILLISECONDS.sleep(consumeSpeed);
                                totalConsumed.incrementAndGet();
                                log.info("消费者 {} 处理完成: {}", consumerId, message.getOrderId());
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            log.error("消费者 {} 处理消息异常", consumerId, e);
                        }
                    }
                    
                    activeConsumers.decrementAndGet();
                    log.info("消费者 {} 已停止", consumerId);
                });
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已调整消费者数量: " + oldCount + " -> " + count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 计算堆积率
     * 正值表示堆积增加，负值表示堆积减少
     */
    private double calculateAccumulationRate() {
        int queueSize = messageQueue.size();
        int produced = totalProduced.get();
        int consumed = totalConsumed.get();
        
        if (produced == 0) {
            return 0;
        }
        
        return (double) queueSize / produced;
    }
    
    /**
     * 创建示例订单消息
     */
    private OrderMessage createSampleOrderMessage() {
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderId(UUID.randomUUID().toString());
        orderMessage.setUserId("user-" + (int)(Math.random() * 1000));
        orderMessage.setAmount(new BigDecimal(String.format("%.2f", Math.random() * 1000)));
        orderMessage.setCreateTime(LocalDateTime.now());
        orderMessage.setStatus("CREATED");
        return orderMessage;
    }
}