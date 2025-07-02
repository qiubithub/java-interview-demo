package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.annotation.DistributedLockable;
import com.qiubithub.threadpool.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class BusinessServiceImpl implements BusinessService {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    @DistributedLockable(prefix = "business", key = "#businessId", leaseTime = 10, waitTime = 5, timeUnit = TimeUnit.SECONDS)
    public String processBusinessWithLock(String businessId) {
        String startTime = LocalDateTime.now().format(formatter);
        log.info("【加锁】开始处理业务: {}, 线程: {}, 开始时间: {}", 
                businessId, Thread.currentThread().getName(), startTime);
        
        try {
            // 模拟业务处理
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("业务处理被中断: {}", businessId, e);
            return "业务处理失败: " + e.getMessage();
        }
        
        String endTime = LocalDateTime.now().format(formatter);
        log.info("【加锁】业务处理完成: {}, 线程: {}, 结束时间: {}", 
                businessId, Thread.currentThread().getName(), endTime);
        return "业务 " + businessId + " 处理成功（使用分布式锁）";
    }

    @Override
    public String processBusinessWithoutLock(String businessId) {
        String startTime = LocalDateTime.now().format(formatter);
        log.info("【无锁】开始处理业务: {}, 线程: {}, 开始时间: {}", 
                businessId, Thread.currentThread().getName(), startTime);
        
        try {
            // 模拟业务处理
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("业务处理被中断: {}", businessId, e);
            return "业务处理失败: " + e.getMessage();
        }
        
        String endTime = LocalDateTime.now().format(formatter);
        log.info("【无锁】业务处理完成: {}, 线程: {}, 结束时间: {}", 
                businessId, Thread.currentThread().getName(), endTime);
        return "业务 " + businessId + " 处理成功（不使用分布式锁）";
    }
} 