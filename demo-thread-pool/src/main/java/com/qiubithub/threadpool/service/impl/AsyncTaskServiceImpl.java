package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.service.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 异步任务服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {

    @Override
    @Async("ioThreadPool")
    public CompletableFuture<String> executeIoTask(String taskId) {
        log.info("开始执行IO密集型任务: {}, 线程: {}", taskId, Thread.currentThread().getName());

        try {
            // 模拟IO操作
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("IO密集型任务执行被中断: {}", taskId, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        log.info("IO密集型任务执行完成: {}", taskId);
        return CompletableFuture.completedFuture("IO密集型任务 " + taskId + " 执行成功");
    }

    @Override
    @Async("cpuThreadPool")
    public CompletableFuture<String> executeCpuTask(String taskId) {
        log.info("开始执行CPU密集型任务: {}, 线程: {}", taskId, Thread.currentThread().getName());

        // 模拟CPU密集型计算
        long result = 0;
        for (int i = 0; i < 100_000_000; i++) {
            result += i;
        }

        log.info("CPU密集型任务执行完成: {}, 计算结果: {}", taskId, result);
        return CompletableFuture.completedFuture("CPU密集型任务 " + taskId + " 执行成功，结果: " + result);
    }

    @Override
    @Async("mixedThreadPool")
    public CompletableFuture<String> executeMixedTask(String taskId) {
        log.info("开始执行混合型任务: {}, 线程: {}", taskId, Thread.currentThread().getName());

        try {
            // 模拟混合型任务
            Thread.sleep(1000)  ;

            // 模拟计算
            long result = 0;
            for (int i = 0; i < 10_000_000; i++) {
                result += i;
            }

            log.info("混合型任务执行完成: {}, 计算结果: {}", taskId, result);
            return CompletableFuture.completedFuture("混合型任务 " + taskId + " 执行成功，结果: " + result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("混合型任务执行被中断: {}", taskId, e);
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
