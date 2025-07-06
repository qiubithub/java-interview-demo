package com.qiubithub.mq.service;

import com.qiubithub.mq.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 临时队列服务，用于在消息堆积时将消息转移到临时队列进行处理
 */
@Slf4j
@Service
public class TemporaryQueueService {

    // 临时队列，用于存储堆积的消息
    private final BlockingQueue<OrderMessage> temporaryQueue = new LinkedBlockingQueue<>(10000);
    
    // 处理临时队列的线程池
    private ExecutorService executorService;
    
    // 是否启用临时队列处理
    private volatile boolean enabled = false;
    
    // 临时队列处理的线程数
    private final int tempQueueThreads = 10;
    
    // 已处理的消息计数
    private final AtomicInteger processedCount = new AtomicInteger(0);
    
    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(tempQueueThreads);
        log.info("临时队列服务初始化完成");
    }
    
    /**
     * 启动临时队列处理
     */
    public void startProcessing() {
        if (enabled) {
            log.info("临时队列处理已经在运行中");
            return;
        }
        
        enabled = true;
        processedCount.set(0);
        
        log.info("启动临时队列处理，线程数: {}", tempQueueThreads);
        
        // 启动多个线程处理临时队列
        for (int i = 0; i < tempQueueThreads; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                log.info("临时队列处理线程 {} 启动", threadId);
                while (enabled) {
                    try {
                        OrderMessage message = temporaryQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (message != null) {
                            // 处理消息
                            processMessage(message, threadId);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("临时队列处理异常", e);
                    }
                }
                log.info("临时队列处理线程 {} 退出", threadId);
            });
        }
    }
    
    /**
     * 停止临时队列处理
     */
    public void stopProcessing() {
        enabled = false;
        log.info("停止临时队列处理，已处理消息数: {}, 剩余消息数: {}", 
                processedCount.get(), temporaryQueue.size());
    }
    
    /**
     * 添加消息到临时队列
     */
    public boolean addToTemporaryQueue(OrderMessage message) {
        try {
            boolean added = temporaryQueue.offer(message, 100, TimeUnit.MILLISECONDS);
            if (!added) {
                log.warn("临时队列已满，消息添加失败: {}", message.getOrderId());
            }
            return added;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("添加消息到临时队列被中断", e);
            return false;
        }
    }
    
    /**
     * 处理临时队列中的消息
     */
    private void processMessage(OrderMessage message, int threadId) {
        try {
            log.info("临时队列线程 {} 处理消息: {}", threadId, message.getOrderId());
            
            // 模拟快速处理
            TimeUnit.MILLISECONDS.sleep(50);
            
            // 增加处理计数
            int count = processedCount.incrementAndGet();
            if (count % 100 == 0) {
                log.info("临时队列已处理消息数: {}", count);
            }
            
            log.info("临时队列线程 {} 处理消息完成: {}", threadId, message.getOrderId());
        } catch (Exception e) {
            log.error("处理临时队列消息异常", e);
        }
    }
    
    /**
     * 获取临时队列大小
     */
    public int getTemporaryQueueSize() {
        return temporaryQueue.size();
    }
    
    /**
     * 获取已处理消息数
     */
    public int getProcessedCount() {
        return processedCount.get();
    }
    
    /**
     * 清空临时队列
     */
    public void clearTemporaryQueue() {
        temporaryQueue.clear();
        log.info("临时队列已清空");
    }
    
    @PreDestroy
    public void destroy() {
        enabled = false;
        if (executorService != null) {
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("临时队列服务已关闭");
    }
}