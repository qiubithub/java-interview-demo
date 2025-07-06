package com.qiubithub.threadpool.factory;

import com.qiubithub.threadpool.handler.LoggingRejectedExecutionHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * <p>
 * 线程池工厂类，提供各种预定义线程池的创建方法
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
public class ThreadPoolFactory {

    /**
     * 创建IO密集型任务线程池
     *
     * @param namePrefix 线程名前缀
     * @return 线程池执行器
     */
    public static ThreadPoolExecutor createIoIntensivePool(String namePrefix) {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        int maxSize = coreSize * 2;
        
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                new NamedThreadFactory(namePrefix + "-io"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        );
    }

    /**
     * 创建CPU密集型任务线程池
     *
     * @param namePrefix 线程名前缀
     * @return 线程池执行器
     */
    public static ThreadPoolExecutor createCpuIntensivePool(String namePrefix) {
        int coreSize = Runtime.getRuntime().availableProcessors() + 1;
        
        return new ThreadPoolExecutor(
                coreSize,
                coreSize * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new NamedThreadFactory(namePrefix + "-cpu"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy())
        );
    }

    /**
     * 创建快速响应线程池，使用SynchronousQueue，适用于需要快速响应的短任务
     *
     * @param namePrefix 线程名前缀
     * @return 线程池执行器
     */
    public static ThreadPoolExecutor createFastResponsePool(String namePrefix) {
        int coreSize = Runtime.getRuntime().availableProcessors();
        
        return new ThreadPoolExecutor(
                coreSize,
                coreSize * 10,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory(namePrefix + "-fast"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        );
    }

    /**
     * 创建定时任务线程池
     *
     * @param namePrefix 线程名前缀
     * @param coreSize   核心线程数
     * @return 定时任务线程池执行器
     */
    public static ScheduledThreadPoolExecutor createScheduledPool(String namePrefix, int coreSize) {
        return new ScheduledThreadPoolExecutor(
                coreSize,
                new NamedThreadFactory(namePrefix + "-scheduled"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
        );
    }

    /**
     * 创建有界队列线程池，队列有限，适合资源敏感的场景
     *
     * @param namePrefix   线程名前缀
     * @param coreSize     核心线程数
     * @param maxSize      最大线程数
     * @param queueSize    队列大小
     * @param keepAliveTime 空闲线程存活时间
     * @param unit         时间单位
     * @return 线程池执行器
     */
    public static ThreadPoolExecutor createBoundedQueuePool(
            String namePrefix, int coreSize, int maxSize, int queueSize, 
            long keepAliveTime, TimeUnit unit) {
        
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                unit,
                new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory(namePrefix + "-bounded"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        );
    }
}