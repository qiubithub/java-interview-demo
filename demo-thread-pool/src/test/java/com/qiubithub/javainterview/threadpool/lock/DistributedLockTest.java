package com.qiubithub.javainterview.threadpool.lock;

import com.qiubithub.threadpool.lock.DistributedLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 分布式锁测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
public class DistributedLockTest {

    @Autowired
    private DistributedLock distributedLock;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("测试获取锁和释放锁")
    void testLockAndUnlock() {
        String lockKey = "test-lock-key";
        
        // 获取锁
        distributedLock.lock(lockKey);
        
        // 验证锁是否存在于Redis
        Boolean hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertTrue(hasKey);
        
        // 释放锁
        distributedLock.unlock(lockKey);
        
        // 验证锁是否已释放
        hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertFalse(hasKey);
    }

    @Test
    @DisplayName("测试尝试获取锁")
    void testTryLock() throws InterruptedException {
        String lockKey = "test-trylock-key";
        
        // 尝试获取锁，应该成功
        boolean acquired = distributedLock.tryLock(lockKey, 5, TimeUnit.SECONDS);
        assertTrue(acquired);
        
        // 验证锁是否存在于Redis
        Boolean hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertTrue(hasKey);
        
        // 释放锁
        distributedLock.unlock(lockKey);
        
        // 验证锁是否已释放
        hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertFalse(hasKey);
    }

    @Test
    @DisplayName("测试尝试获取已被占用的锁")
    void testTryLockWhenLocked() throws InterruptedException {
        String lockKey = "test-trylock-locked-key";
        
        // 先获取锁
        distributedLock.lock(lockKey);
        
        // 在另一个线程中尝试获取同一个锁，应该失败
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> {
            return distributedLock.tryLock(lockKey, 1, TimeUnit.SECONDS);
        });
        
        boolean acquired;
        try {
            acquired = future.get();
        } catch (ExecutionException e) {
            acquired = false;
        }
        
        // 应该获取失败
        assertFalse(acquired);
        
        // 释放锁
        distributedLock.unlock(lockKey);
        
        executor.shutdown();
    }

    @Test
    @DisplayName("测试锁的过期时间")
    void testLockExpiration() throws InterruptedException {
        String lockKey = "test-lock-expiration";
        
        // 获取锁，设置过期时间为1秒
        boolean acquired = distributedLock.tryLock(lockKey, 1, 5, TimeUnit.SECONDS);
        assertTrue(acquired);
        
        // 验证锁是否存在于Redis
        Boolean hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertTrue(hasKey);
        
        // 等待锁过期
        Thread.sleep(1500);
        
        // 验证锁是否已过期
        hasKey = redisTemplate.hasKey("distributed_lock:" + lockKey);
        assertFalse(hasKey);
    }

    @Test
    @DisplayName("测试并发获取锁")
    void testConcurrentLockAcquisition() throws InterruptedException {
        String lockKey = "test-concurrent-lock";
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // 创建一个共享计数器，用于验证只有一个线程能够获取锁并执行临界区代码
        AtomicInteger sharedCounter = new AtomicInteger(0);
        List<Integer> counterValues = new ArrayList<>();
        
        // 提交多个任务
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 等待所有线程准备就绪
                    startLatch.await();
                    
                    // 尝试获取锁
                    boolean acquired = distributedLock.tryLock(lockKey, 5, TimeUnit.SECONDS);
                    if (acquired) {
                        try {
                            successCount.incrementAndGet();
                            
                            // 模拟临界区操作
                            int value = sharedCounter.incrementAndGet();
                            Thread.sleep(100);  // 模拟一些工作
                            counterValues.add(value);
                        } finally {
                            distributedLock.unlock(lockKey);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
        }
        
        // 启动所有线程
        startLatch.countDown();
        
        // 等待所有线程完成
        boolean completed = completionLatch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "所有线程应该在30秒内完成");
        
        // 验证只有一个线程成功获取了锁
        assertEquals(1, successCount.get());
        
        // 验证共享计数器的值
        assertEquals(1, counterValues.size());
        assertEquals(1, counterValues.get(0).intValue());
        
        executorService.shutdown();
    }
}