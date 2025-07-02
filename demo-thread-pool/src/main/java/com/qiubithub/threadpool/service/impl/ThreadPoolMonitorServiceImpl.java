package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.model.ThreadPoolStats;
import com.qiubithub.threadpool.service.ThreadPoolMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 线程池监控服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class ThreadPoolMonitorServiceImpl implements ThreadPoolMonitorService {

    /**
     * Spring容器中的线程池映射，key为线程池名称，value为线程池对象
     */
    private final Map<String, ThreadPoolTaskExecutor> threadPoolMap = new HashMap<>();

    /**
     * 构造函数，注入所有ThreadPoolTaskExecutor类型的bean
     *
     * @param threadPools 线程池列表
     */
    @Autowired
    public ThreadPoolMonitorServiceImpl(Map<String, ThreadPoolTaskExecutor> threadPools) {
        this.threadPoolMap.putAll(threadPools);
        log.info("线程池监控服务已初始化，共发现{}个线程池", threadPools.size());
    }

    @Override
    public ThreadPoolStats getThreadPoolStats(String poolName) {
        if (StringUtils.isEmpty(poolName) || !threadPoolMap.containsKey(poolName)) {
            log.warn("线程池不存在：{}", poolName);
            return null;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = threadPoolMap.get(poolName);
        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();
        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();

        return buildThreadPoolStats(poolName, threadPoolExecutor, queue);
    }

    @Override
    public List<ThreadPoolStats> getAllThreadPoolStats() {
        List<ThreadPoolStats> statsList = new ArrayList<>();

        for (Map.Entry<String, ThreadPoolTaskExecutor> entry : threadPoolMap.entrySet()) {
            String poolName = entry.getKey();
            ThreadPoolTaskExecutor threadPoolTaskExecutor = entry.getValue();
            ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();
            BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();

            ThreadPoolStats stats = buildThreadPoolStats(poolName, threadPoolExecutor, queue);
            statsList.add(stats);
        }

        return statsList;
    }

    @Override
    public ThreadPoolStats updateThreadPoolConfig(String poolName, Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity) {
        if (StringUtils.isEmpty(poolName) || !threadPoolMap.containsKey(poolName)) {
            log.warn("线程池不存在：{}", poolName);
            return null;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = threadPoolMap.get(poolName);
        ThreadPoolExecutor threadPoolExecutor = threadPoolTaskExecutor.getThreadPoolExecutor();

        // 记录原始配置
        int oldCorePoolSize = threadPoolExecutor.getCorePoolSize();
        int oldMaxPoolSize = threadPoolExecutor.getMaximumPoolSize();

        // 更新配置
        if (corePoolSize != null && corePoolSize > 0) {
            if (maxPoolSize != null && corePoolSize > maxPoolSize) {
                log.warn("核心线程数不能大于最大线程数，自动调整为最大线程数: {}", maxPoolSize);
                corePoolSize = maxPoolSize;
            }
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            log.info("线程池[{}]核心线程数由{}调整为{}", poolName, oldCorePoolSize, corePoolSize);
        }

        if (maxPoolSize != null && maxPoolSize > 0) {
            if (maxPoolSize < threadPoolExecutor.getCorePoolSize()) {
                threadPoolExecutor.setCorePoolSize(maxPoolSize);
                log.info("线程池[{}]核心线程数已自动调整为{}", poolName, maxPoolSize);
            }
            threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            log.info("线程池[{}]最大线程数由{}调整为{}", poolName, oldMaxPoolSize, maxPoolSize);
        }

        // 注意：大多数队列不支持动态调整容量
        if (queueCapacity != null && queueCapacity > 0) {
            log.warn("大多数队列不支持动态调整容量，此配置可能无效");
        }

        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        return buildThreadPoolStats(poolName, threadPoolExecutor, queue);
    }

    /**
     * 构建线程池统计信息
     *
     * @param poolName          线程池名称
     * @param threadPoolExecutor 线程池执行器
     * @param queue             等待队列
     * @return 线程池统计信息
     */
    private ThreadPoolStats buildThreadPoolStats(String poolName, ThreadPoolExecutor threadPoolExecutor, BlockingQueue<Runnable> queue) {
        return ThreadPoolStats.builder()
                .poolName(poolName)
                .corePoolSize(threadPoolExecutor.getCorePoolSize())
                .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                .poolSize(threadPoolExecutor.getPoolSize())
                .activeCount(threadPoolExecutor.getActiveCount())
                .queueSize(queue.size())
                .completedTaskCount(threadPoolExecutor.getCompletedTaskCount())
                .taskCount(threadPoolExecutor.getTaskCount())
                .isShutdown(threadPoolExecutor.isShutdown())
                .isTerminated(threadPoolExecutor.isTerminated())
                .remainingQueueCapacity(queue.remainingCapacity())
                .build();
    }
}