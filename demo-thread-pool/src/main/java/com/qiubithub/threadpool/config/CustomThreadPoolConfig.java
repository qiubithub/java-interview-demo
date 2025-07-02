package com.qiubithub.threadpool.config;

import com.qiubithub.threadpool.factory.NamedThreadFactory;
import com.qiubithub.threadpool.handler.LoggingRejectedExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * <p>
 * 自定义线程池配置类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Configuration
public class CustomThreadPoolConfig {

    /**
     * 快速响应线程池
     *
     * @return 线程池执行器
     */
    @Bean("fastThreadPool")
    public ThreadPoolExecutor fastThreadPool() {
        int coreSize = Runtime.getRuntime().availableProcessors();
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize,
                coreSize * 10,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("fast-task"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        );
        
        // 允许核心线程超时
        executor.allowCoreThreadTimeOut(true);
        
        log.info("快速响应线程池初始化完成");
        return executor;
    }

    /**
     * 有界队列线程池
     *
     * @return 线程池执行器
     */
    @Bean("boundedThreadPool")
    public ThreadPoolExecutor boundedThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new NamedThreadFactory("bounded-task"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy())
        );
        
        log.info("有界队列线程池初始化完成");
        return executor;
    }

    /**
     * 无界队列线程池
     *
     * @return 线程池执行器
     */
    @Bean("unboundedThreadPool")
    public ThreadPoolExecutor unboundedThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3,
                3, // 使用无界队列时，maxSize参数无效
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("unbounded-task")
        );
        
        log.info("无界队列线程池初始化完成");
        return executor;
    }

    /**
     * 定时任务线程池
     *
     * @return 定时任务线程池执行器
     */
    @Bean("scheduledPool")
    public ScheduledThreadPoolExecutor scheduledThreadPool() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
                2,
                new NamedThreadFactory("scheduled-pool-task"),
                new LoggingRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
        );
        
        // 设置移除已取消任务的策略
        executor.setRemoveOnCancelPolicy(true);
        
        log.info("定时任务线程池初始化完成");
        return executor;
    }
}