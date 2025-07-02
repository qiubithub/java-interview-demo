package com.qiubithub.javainterview.threadpool.service;

import com.qiubithub.threadpool.service.BusinessService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 业务服务测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
public class BusinessServiceTest {

    @Autowired
    private BusinessService businessService;

    @Test
    @DisplayName("测试使用分布式锁处理业务")
    void testProcessBusinessWithLock() {
        String result = businessService.processBusinessWithLock("test-business-1");
        
        assertNotNull(result);
        assertTrue(result.contains("业务 test-business-1 处理成功"));
        assertTrue(result.contains("使用分布式锁"));
    }

    @Test
    @DisplayName("测试不使用分布式锁处理业务")
    void testProcessBusinessWithoutLock() {
        String result = businessService.processBusinessWithoutLock("test-business-2");
        
        assertNotNull(result);
        assertTrue(result.contains("业务 test-business-2 处理成功"));
        assertTrue(result.contains("不使用分布式锁"));
    }

    @Test
    @DisplayName("测试并发使用分布式锁处理业务")
    void testConcurrentProcessBusinessWithLock() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<String> results = new ArrayList<>();
        
        // 使用相同的业务ID，模拟并发访问
        String businessId = "concurrent-business";
        
        // 提交多个任务
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    String result = businessService.processBusinessWithLock(businessId);
                    synchronized (results) {
                        results.add(result);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应该在30秒内完成");
        
        // 验证结果
        assertEquals(threadCount, results.size());
        for (String result : results) {
            assertTrue(result.contains("业务 " + businessId + " 处理成功") || result.contains("系统繁忙"));
        }
        
        executorService.shutdown();
    }

    @Test
    @DisplayName("测试并发不使用分布式锁处理业务")
    void testConcurrentProcessBusinessWithoutLock() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<String> results = new ArrayList<>();
        
        // 使用相同的业务ID，模拟并发访问
        String businessId = "concurrent-business-no-lock";
        
        // 提交多个任务
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    String result = businessService.processBusinessWithoutLock(businessId);
                    synchronized (results) {
                        results.add(result);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应该在30秒内完成");
        
        // 验证结果
        assertEquals(threadCount, results.size());
        for (String result : results) {
            assertTrue(result.contains("业务 " + businessId + " 处理成功"));
            assertTrue(result.contains("不使用分布式锁"));
        }
        
        executorService.shutdown();
    }

    @Test
    @DisplayName("测试分布式锁和无锁的性能对比")
    void testPerformanceComparison() throws InterruptedException {
        int iterations = 5;
        long withLockTotalTime = 0;
        long withoutLockTotalTime = 0;
        
        for (int i = 0; i < iterations; i++) {
            // 测试使用分布式锁
            long startTime = System.currentTimeMillis();
            businessService.processBusinessWithLock("perf-test-" + i);
            long withLockTime = System.currentTimeMillis() - startTime;
            withLockTotalTime += withLockTime;
            
            // 测试不使用分布式锁
            startTime = System.currentTimeMillis();
            businessService.processBusinessWithoutLock("perf-test-" + i);
            long withoutLockTime = System.currentTimeMillis() - startTime;
            withoutLockTotalTime += withoutLockTime;
            
            System.out.println("迭代 " + i + ": 使用锁耗时 " + withLockTime + "ms, 不使用锁耗时 " + withoutLockTime + "ms");
        }
        
        double withLockAvg = (double) withLockTotalTime / iterations;
        double withoutLockAvg = (double) withoutLockTotalTime / iterations;
        
        System.out.println("使用分布式锁平均耗时: " + withLockAvg + "ms");
        System.out.println("不使用分布式锁平均耗时: " + withoutLockAvg + "ms");
        System.out.println("性能差异: " + (withLockAvg - withoutLockAvg) + "ms");
    }
}