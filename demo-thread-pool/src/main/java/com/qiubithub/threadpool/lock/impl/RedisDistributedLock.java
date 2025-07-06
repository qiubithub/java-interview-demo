package com.qiubithub.threadpool.lock.impl;

import com.qiubithub.threadpool.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 基于Redisson的分布式锁实现
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;
    
    /**
     * 锁前缀
     */
    private static final String LOCK_PREFIX = "distributed_lock:";

    @Override
    public void lock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        
        try {
            lock.lock();
            log.debug("获取分布式锁成功，key: {}", key);
        } catch (Exception e) {
            log.error("获取分布式锁失败，key: {}", key, e);
            throw new RuntimeException("获取分布式锁失败", e);
        }
    }

    @Override
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        
        try {
            // 只有当前线程持有锁时才能释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁成功，key: {}", key);
            } else {
                log.warn("当前线程未持有锁，无法释放，key: {}", key);
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，key: {}", key, e);
        }
    }

    @Override
    public boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        
        try {
            boolean acquired = lock.tryLock(timeout, unit);
            if (acquired) {
                log.debug("尝试获取分布式锁成功，key: {}", key);
            } else {
                log.debug("尝试获取分布式锁失败，key: {}", key);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("尝试获取分布式锁时线程被中断，key: {}", key, e);
            return false;
        } catch (Exception e) {
            log.error("尝试获取分布式锁失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public boolean tryLock(String lockKey, long leaseTime, long timeout, TimeUnit unit) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        
        try {
            boolean acquired = lock.tryLock(timeout, leaseTime, unit);
            if (acquired) {
                log.debug("尝试获取分布式锁成功，key: {}, leaseTime: {}, timeout: {}", key, leaseTime, timeout);
            } else {
                log.debug("尝试获取分布式锁失败，key: {}, leaseTime: {}, timeout: {}", key, leaseTime, timeout);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("尝试获取分布式锁时线程被中断，key: {}", key, e);
            return false;
        } catch (Exception e) {
            log.error("尝试获取分布式锁失败，key: {}", key, e);
            return false;
        }
    }
    
    /**
     * 获取RLock对象，供高级用法使用
     * 
     * @param lockKey 锁键
     * @return RLock对象
     */
    public RLock getRLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        return redissonClient.getLock(key);
    }
    
    /**
     * 检查锁是否被持有
     * 
     * @param lockKey 锁键
     * @return 是否被持有
     */
    public boolean isLocked(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        return lock.isLocked();
    }
    
    /**
     * 检查锁是否被当前线程持有
     * 
     * @param lockKey 锁键
     * @return 是否被当前线程持有
     */
    public boolean isHeldByCurrentThread(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(key);
        return lock.isHeldByCurrentThread();
    }
} 