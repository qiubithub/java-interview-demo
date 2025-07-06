package com.qiubithub.threadpool.lock;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 分布式锁接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface DistributedLock {

    /**
     * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止
     *
     * @param lockKey 锁的键值
     */
    void lock(String lockKey);

    /**
     * 释放锁
     *
     * @param lockKey 锁的键值
     */
    void unlock(String lockKey);

    /**
     * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止，如果在指定时间内获取锁失败，则返回false
     *
     * @param lockKey 锁的键值
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long timeout, TimeUnit unit);

    /**
     * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止，如果在指定时间内获取锁失败，则返回false
     *
     * @param lockKey   锁的键值
     * @param leaseTime 上锁后自动释放锁的时间
     * @param timeout   获取锁的超时时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long leaseTime, long timeout, TimeUnit unit);
} 