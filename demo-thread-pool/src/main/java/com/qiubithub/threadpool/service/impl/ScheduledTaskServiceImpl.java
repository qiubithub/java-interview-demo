package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.factory.NamedThreadFactory;
import com.qiubithub.threadpool.service.ScheduledTaskService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 定时任务服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    /**
     * 定时任务线程池
     */
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            2,
            new NamedThreadFactory("scheduled-task")
    );

    /**
     * 是否运行标识
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        // 初始化配置
        executor.setRemoveOnCancelPolicy(true);
        log.info("定时任务服务初始化完成");
    }

    @PreDestroy
    public void destroy() {
        stopScheduledTasks();
        executor.shutdown();
        log.info("定时任务服务已关闭");
    }

    @Override
    public void startScheduledTasks() {
        if (running.compareAndSet(false, true)) {
            // 任务1: 固定频率执行
            executor.scheduleAtFixedRate(() -> {
                try {
                    log.info("执行固定频率任务: {}", System.currentTimeMillis());
                    // 模拟任务执行
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("固定频率任务执行异常", e);
                }
            }, 0, 5, TimeUnit.SECONDS);

            // 任务2: 固定延迟执行
            executor.scheduleWithFixedDelay(() -> {
                try {
                    log.info("执行固定延迟任务: {}", System.currentTimeMillis());
                    // 模拟任务执行
                    Thread.sleep(200);
                } catch (Exception e) {
                    log.error("固定延迟任务执行异常", e);
                }
            }, 2, 10, TimeUnit.SECONDS);

            log.info("定时任务已启动");
        } else {
            log.info("定时任务已在运行中");
        }
    }

    @Override
    public void stopScheduledTasks() {
        if (running.compareAndSet(true, false)) {
            executor.getQueue().clear();
            log.info("定时任务已停止");
        } else {
            log.info("定时任务未在运行");
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}