package com.qiubithub.mq.controller;

import com.qiubithub.mq.domain.OrderMessage;
import com.qiubithub.mq.service.MessageAccumulationHandler;
import com.qiubithub.mq.service.OrderMessageProducer;
import com.qiubithub.mq.service.TemporaryQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private OrderMessageProducer orderMessageProducer;
    
    @Autowired
    private MessageAccumulationHandler messageAccumulationHandler;
    
    @Autowired
    private TemporaryQueueService temporaryQueueService;

    /**
     * 发送单个订单消息
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody(required = false) OrderMessage orderMessage) {
        if (orderMessage == null) {
            orderMessage = createSampleOrderMessage();
        }
        
        SendResult result = orderMessageProducer.sendOrderMessage(orderMessage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("sendStatus", result.getSendStatus());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 发送优先级订单消息
     */
    @PostMapping("/send/priority")
    public ResponseEntity<Map<String, Object>> sendPriorityMessage(@RequestBody(required = false) OrderMessage orderMessage) {
        if (orderMessage == null) {
            orderMessage = createSampleOrderMessage();
            orderMessage.setStatus("PRIORITY");
        }
        
        SendResult result = orderMessageProducer.sendPriorityOrderMessage(orderMessage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", result.getMsgId());
        response.put("orderId", orderMessage.getOrderId());
        response.put("sendStatus", result.getSendStatus());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量生成消息，模拟消息堆积
     */
    @PostMapping("/generate/{count}")
    public ResponseEntity<Map<String, Object>> generateBulkMessages(@PathVariable int count) {
        // 异步执行，避免请求超时
        CompletableFuture.runAsync(() -> {
            orderMessageProducer.generateBulkMessages(count);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "开始生成 " + count + " 条消息");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 切换消息堆积处理策略
     */
    @PostMapping("/strategy/{strategy}")
    public ResponseEntity<Map<String, Object>> switchStrategy(@PathVariable String strategy) {
        MessageAccumulationHandler.AccumulationStrategy newStrategy;
        
        try {
            newStrategy = MessageAccumulationHandler.AccumulationStrategy.valueOf(strategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "无效的策略: " + strategy);
            error.put("validStrategies", MessageAccumulationHandler.AccumulationStrategy.values());
            return ResponseEntity.badRequest().body(error);
        }
        
        messageAccumulationHandler.switchToAccumulationStrategy(newStrategy);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已切换到策略: " + newStrategy);
        response.put("currentStrategy", messageAccumulationHandler.getCurrentStrategy());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取当前状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("accumulationStrategy", messageAccumulationHandler.getCurrentStrategy());
        status.put("statistics", messageAccumulationHandler.getStatistics());
        status.put("temporaryQueueSize", temporaryQueueService.getTemporaryQueueSize());
        status.put("temporaryQueueProcessed", temporaryQueueService.getProcessedCount());
        
        return ResponseEntity.ok(status);
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