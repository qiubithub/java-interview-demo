package com.qiubithub.threadpool.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 分布式锁注解
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLockable {

    /**
     * 锁的键前缀
     */
    String prefix() default "lock";

    /**
     * 锁的名称，支持SpEL表达式
     */
    String key();

    /**
     * 锁的过期时间（租约时间）
     * -1表示使用Redisson默认的看门狗机制（自动续期）
     */
    long leaseTime() default -1;

    /**
     * 获取锁的等待时间
     */
    long waitTime() default 10;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 锁类型
     */
    LockType lockType() default LockType.REENTRANT_LOCK;

    /**
     * 获取锁失败时是否抛出异常
     * true: 抛出异常
     * false: 返回默认值或null
     */
    boolean failFast() default true;

    /**
     * 获取锁失败时的默认返回值（仅当failFast=false时生效）
     * 支持SpEL表达式
     */
    String fallbackValue() default "";

    /**
     * 是否公平锁
     * 仅对FAIR_LOCK类型生效
     */
    boolean fair() default false;

    /**
     * 锁类型枚举
     */
    enum LockType {
        /**
         * 可重入锁（默认）
         */
        REENTRANT_LOCK,
        
        /**
         * 公平锁
         */
        FAIR_LOCK,
        
        /**
         * 读写锁-读锁
         */
        READ_LOCK,
        
        /**
         * 读写锁-写锁
         */
        WRITE_LOCK,
        
        /**
         * 信号量
         */
        SEMAPHORE
    }
} 