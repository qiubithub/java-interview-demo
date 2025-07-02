package com.qiubithub.threadpool.lock.impl;

import com.qiubithub.threadpool.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 基于Redis的分布式锁实现
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock implements DistributedLock {

    private final StringRedisTemplate redisTemplate;
    
    /**
     * 默认锁过期时间（秒）
     */
    private static final long DEFAULT_EXPIRE = 30;
    
    /**
     * 锁前缀
     */
    private static final String LOCK_PREFIX = "distributed_lock:";
    
    /**
     * 线程标识前缀
     */
    private static final String UUID_PREFIX = UUID.randomUUID().toString().replace("-", "") + "-";
    
    /**
     * 线程变量，存储线程标识
     */
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    /**
     * 释放锁的Lua脚本
     */
    private static final String UNLOCK_LUA_SCRIPT = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('del', KEYS[1]) " +
            "else " +
            "   return 0 " +
            "end";

    @Override
    public void lock(String lockKey) {
        String threadId = UUID_PREFIX + Thread.currentThread().getId();
        threadLocal.set(threadId);
        
        String key = LOCK_PREFIX + lockKey;
        
        // 尝试获取锁，直到成功
        while (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, threadId, DEFAULT_EXPIRE, TimeUnit.SECONDS))) {
            try {
                // 短暂休眠，避免CPU空转
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("获取分布式锁时线程被中断", e);
                throw new RuntimeException("获取分布式锁失败", e);
            }
        }
    }

    @Override
    public void unlock(String lockKey) {
        String threadId = threadLocal.get();
        if (Objects.isNull(threadId)) {
            return;
        }
        
        try {
            String key = LOCK_PREFIX + lockKey;
            // 使用Lua脚本保证原子性操作
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), threadId);
            
            if (Objects.equals(result, 0L)) {
                log.warn("释放分布式锁失败，可能已超时或被其他线程释放, key: {}", key);
            }
        } finally {
            // 清理线程变量
            threadLocal.remove();
        }
    }

    @Override
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        return tryLock(lockKey, DEFAULT_EXPIRE, timeout, unit);
    }

    @Override
    public boolean tryLock(String lockKey, long leaseTime, long timeout, TimeUnit unit) {
        String threadId = UUID_PREFIX + Thread.currentThread().getId();
        threadLocal.set(threadId);
        
        String key = LOCK_PREFIX + lockKey;
        long startTime = System.currentTimeMillis();
        long millisTimeout = unit.toMillis(timeout);
        
        try {
            // 在超时时间内尝试获取锁
            while (System.currentTimeMillis() - startTime < millisTimeout) {
                if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, threadId, leaseTime, unit))) {
                    return true;
                }
                
                // 短暂休眠，避免CPU空转
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁时线程被中断", e);
        }
        
        return false;
    }
} 