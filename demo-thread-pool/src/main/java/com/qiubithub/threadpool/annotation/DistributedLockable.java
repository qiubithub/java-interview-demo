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
     * 锁的过期时间
     */
    long leaseTime() default 30;

    /**
     * 获取锁的等待时间
     */
    long waitTime() default 10;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
} 