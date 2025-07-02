package com.qiubithub.threadpool.model;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 线程池统计信息
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Data
@Builder
public class ThreadPoolStats {
    
    /**
     * 线程池名称
     */
    private String poolName;
    
    /**
     * 核心线程数
     */
    private int corePoolSize;
    
    /**
     * 最大线程数
     */
    private int maximumPoolSize;
    
    /**
     * 当前线程池大小
     */
    private int poolSize;
    
    /**
     * 活跃线程数
     */
    private int activeCount;
    
    /**
     * 等待队列长度
     */
    private int queueSize;
    
    /**
     * 任务完成数
     */
    private long completedTaskCount;
    
    /**
     * 任务总数
     */
    private long taskCount;
    
    /**
     * 是否已关闭
     */
    private boolean isShutdown;
    
    /**
     * 是否已终止
     */
    private boolean isTerminated;
    
    /**
     * 队列剩余容量
     */
    private int remainingQueueCapacity;
} 