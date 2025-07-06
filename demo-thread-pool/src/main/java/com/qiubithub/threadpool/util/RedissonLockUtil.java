package com.qiubithub.threadpool.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * <p>
 * Redisson分布式锁工具类
 * 提供编程式的分布式锁使用方式
 * </p>
 *
 * @author qiuchuanze
 * @date 2024/12/06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockUtil {

    private final RedissonClient redissonClient;

    /**
     * 执行带锁的业务逻辑（阻塞式获取锁）
     *
     * @param lockKey  锁键
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            log.debug("获取分布式锁成功，key: {}", lockKey);
            return supplier.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 尝试执行带锁的业务逻辑（非阻塞式）
     *
     * @param lockKey     锁键
     * @param waitTime    等待时间
     * @param leaseTime   租约时间
     * @param timeUnit    时间单位
     * @param supplier    业务逻辑
     * @param defaultValue 获取锁失败时的默认返回值
     * @param <T>         返回值类型
     * @return 执行结果
     */
    public <T> T tryExecuteWithLock(String lockKey, long waitTime, long leaseTime, 
                                   TimeUnit timeUnit, Supplier<T> supplier, T defaultValue) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                log.debug("获取分布式锁成功，key: {}", lockKey);
                return supplier.get();
            } else {
                log.warn("获取分布式锁失败，key: {}", lockKey);
                return defaultValue;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁时线程被中断，key: {}", lockKey, e);
            return defaultValue;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 执行带公平锁的业务逻辑
     *
     * @param lockKey  锁键
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithFairLock(String lockKey, Supplier<T> supplier) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        fairLock.lock();
        try {
            log.debug("获取公平锁成功，key: {}", lockKey);
            return supplier.get();
        } finally {
            if (fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
                log.debug("释放公平锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 执行带读锁的业务逻辑
     *
     * @param lockKey  锁键
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithReadLock(String lockKey, Supplier<T> supplier) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(lockKey);
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            log.debug("获取读锁成功，key: {}", lockKey);
            return supplier.get();
        } finally {
            if (readLock.isHeldByCurrentThread()) {
                readLock.unlock();
                log.debug("释放读锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 执行带写锁的业务逻辑
     *
     * @param lockKey  锁键
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithWriteLock(String lockKey, Supplier<T> supplier) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(lockKey);
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            log.debug("获取写锁成功，key: {}", lockKey);
            return supplier.get();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
                log.debug("释放写锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 执行带信号量的业务逻辑
     *
     * @param semaphoreKey 信号量键
     * @param permits      许可数量
     * @param supplier     业务逻辑
     * @param <T>          返回值类型
     * @return 执行结果
     */
    public <T> T executeWithSemaphore(String semaphoreKey, int permits, Supplier<T> supplier) {
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        try {
            semaphore.acquire(permits);
            log.debug("获取信号量成功，key: {}, permits: {}", semaphoreKey, permits);
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取信号量时线程被中断，key: {}", semaphoreKey, e);
            throw new RuntimeException("获取信号量失败", e);
        } finally {
            semaphore.release(permits);
            log.debug("释放信号量，key: {}, permits: {}", semaphoreKey, permits);
        }
    }

    /**
     * 尝试执行带信号量的业务逻辑
     *
     * @param semaphoreKey 信号量键
     * @param permits      许可数量
     * @param waitTime     等待时间
     * @param timeUnit     时间单位
     * @param supplier     业务逻辑
     * @param defaultValue 获取信号量失败时的默认返回值
     * @param <T>          返回值类型
     * @return 执行结果
     */
    public <T> T tryExecuteWithSemaphore(String semaphoreKey, int permits, long waitTime, 
                                        TimeUnit timeUnit, Supplier<T> supplier, T defaultValue) {
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        try {
            if (semaphore.tryAcquire(permits, waitTime, timeUnit)) {
                log.debug("获取信号量成功，key: {}, permits: {}", semaphoreKey, permits);
                return supplier.get();
            } else {
                log.warn("获取信号量失败，key: {}, permits: {}", semaphoreKey, permits);
                return defaultValue;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取信号量时线程被中断，key: {}", semaphoreKey, e);
            return defaultValue;
        } finally {
            semaphore.release(permits);
            log.debug("释放信号量，key: {}, permits: {}", semaphoreKey, permits);
        }
    }

    /**
     * 执行带倒计时锁存器的业务逻辑
     *
     * @param latchKey 锁存器键
     * @param count    计数
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    public <T> T executeWithCountDownLatch(String latchKey, long count, Supplier<T> supplier) {
        RCountDownLatch latch = redissonClient.getCountDownLatch(latchKey);
        try {
            latch.trySetCount(count);
            log.debug("设置倒计时锁存器，key: {}, count: {}", latchKey, count);
            T result = supplier.get();
            latch.await();
            log.debug("倒计时锁存器等待完成，key: {}", latchKey);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待倒计时锁存器时线程被中断，key: {}", latchKey, e);
            throw new RuntimeException("等待倒计时锁存器失败", e);
        }
    }

    /**
     * 获取锁信息
     *
     * @param lockKey 锁键
     * @return 锁信息
     */
    public String getLockInfo(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        return String.format("Lock[key=%s, locked=%s, heldByCurrentThread=%s, holdCount=%d, remainTimeToLive=%d]",
                lockKey, lock.isLocked(), lock.isHeldByCurrentThread(), 
                lock.getHoldCount(), lock.remainTimeToLive());
    }
} 