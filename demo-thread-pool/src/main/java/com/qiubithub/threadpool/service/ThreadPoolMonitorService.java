package com.qiubithub.threadpool.service;

import com.qiubithub.threadpool.model.ThreadPoolStats;

import java.util.List;

/**
 * <p>
 * 线程池监控服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface ThreadPoolMonitorService {

    /**
     * 获取指定名称的线程池统计信息
     *
     * @param poolName 线程池名称
     * @return 线程池统计信息
     */
    ThreadPoolStats getThreadPoolStats(String poolName);

    /**
     * 获取所有线程池的统计信息
     *
     * @return 所有线程池的统计信息
     */
    List<ThreadPoolStats> getAllThreadPoolStats();

    /**
     * 动态调整线程池核心参数
     *
     * @param poolName      线程池名称
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param queueCapacity 队列容量（可选，不是所有线程池都支持调整队列容量）
     * @return 调整后的线程池统计信息
     */
    ThreadPoolStats updateThreadPoolConfig(String poolName, Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity);
} 