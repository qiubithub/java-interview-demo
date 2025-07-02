package com.qiubithub.threadpool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 线程池配置类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    /**
     * IO密集型任务线程池
     * 
     * @return IO密集型任务线程池
     */
    @Bean("ioThreadPool")
    public Executor ioThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数 = CPU核心数 * 2
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数 = 核心线程数 * 2
        executor.setMaxPoolSize(corePoolSize * 2);
        // 队列容量
        executor.setQueueCapacity(500);
        // 线程名前缀
        executor.setThreadNamePrefix("io-task-");
        // 线程存活时间
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * CPU密集型任务线程池
     * 
     * @return CPU密集型任务线程池
     */
    @Bean("cpuThreadPool")
    public Executor cpuThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数 = CPU核心数 + 1
        int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数 = CPU核心数 * 2
        executor.setMaxPoolSize(corePoolSize * 2);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("cpu-task-");
        // 线程存活时间
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 混合型任务线程池
     * 
     * @return 混合型任务线程池
     */
    @Bean("mixedThreadPool")
    public Executor mixedThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(300);
        // 线程名前缀
        executor.setThreadNamePrefix("mixed-task-");
        // 线程存活时间
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }
} 