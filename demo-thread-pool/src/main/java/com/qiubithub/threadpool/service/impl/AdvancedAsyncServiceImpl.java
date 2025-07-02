package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.service.AdvancedAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * <p>
 * 高级异步服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class AdvancedAsyncServiceImpl implements AdvancedAsyncService {

    private final Executor cpuThreadPool;
    private final Executor ioThreadPool;
    private final Executor mixedThreadPool;

    /**
     * 构造函数
     *
     * @param cpuThreadPool   CPU密集型线程池
     * @param ioThreadPool    IO密集型线程池
     * @param mixedThreadPool 混合型线程池
     */
    public AdvancedAsyncServiceImpl(
            @Qualifier("cpuThreadPool") Executor cpuThreadPool,
            @Qualifier("ioThreadPool") Executor ioThreadPool,
            @Qualifier("mixedThreadPool") Executor mixedThreadPool) {
        this.cpuThreadPool = cpuThreadPool;
        this.ioThreadPool = ioThreadPool;
        this.mixedThreadPool = mixedThreadPool;
    }

    @Override
    public CompletableFuture<List<String>> processListParallel(List<String> dataList) {
        log.info("开始并行处理数据列表，大小: {}", dataList.size());

        List<CompletableFuture<String>> futures = dataList.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> {
                    log.info("处理数据项: {}, 线程: {}", item, Thread.currentThread().getName());
                    try {
                        // 模拟处理时间
                        Thread.sleep(100);
                        return "Processed: " + item;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("数据处理被中断: {}", item, e);
                        throw new RuntimeException("处理被中断", e);
                    }
                }, cpuThreadPool))
                .collect(Collectors.toList());

        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        // 收集所有结果
        return allFutures.thenApplyAsync(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()),
                cpuThreadPool);
    }

    @Override
    public CompletableFuture<String> processChainedTasks(String input) {
        log.info("开始链式处理任务: {}", input);

        return CompletableFuture.supplyAsync(() -> {
                    log.info("第一阶段处理: {}, 线程: {}", input, Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                        return input.toUpperCase();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("第一阶段处理被中断", e);
                    }
                }, cpuThreadPool)
                .thenApplyAsync(result -> {
                    log.info("第二阶段处理: {}, 线程: {}", result, Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                        return "Stage2: " + result;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("第二阶段处理被中断", e);
                    }
                }, ioThreadPool)
                .thenApplyAsync(result -> {
                    log.info("第三阶段处理: {}, 线程: {}", result, Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                        return "Final: " + result + "!";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("第三阶段处理被中断", e);
                    }
                }, mixedThreadPool);
    }

    @Override
    public CompletableFuture<String> combineAsyncTasks(String param1, String param2) {
        log.info("开始组合异步任务: {} 和 {}", param1, param2);

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            log.info("处理参数1: {}, 线程: {}", param1, Thread.currentThread().getName());
            try {
                Thread.sleep(200);
                return "Result1: " + param1;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("处理参数1被中断", e);
            }
        }, ioThreadPool);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            log.info("处理参数2: {}, 线程: {}", param2, Thread.currentThread().getName());
            try {
                Thread.sleep(300);
                return "Result2: " + param2;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("处理参数2被中断", e);
            }
        }, cpuThreadPool);

        // 组合两个异步任务的结果
        return future1.thenCombineAsync(future2, (result1, result2) -> {
            log.info("组合结果: {} 和 {}, 线程: {}", result1, result2, Thread.currentThread().getName());
            return result1 + " + " + result2;
        }, mixedThreadPool);
    }

    @Override
    public CompletableFuture<String> executeWithErrorHandling(String input) {
        log.info("开始带异常处理的异步任务: {}", input);

        return CompletableFuture.supplyAsync(() -> {
                    if (input == null || input.isEmpty()) {
                        throw new IllegalArgumentException("输入不能为空");
                    }
                    
                    log.info("处理输入: {}, 线程: {}", input, Thread.currentThread().getName());
                    try {
                        Thread.sleep(200);
                        
                        // 模拟随机异常
                        if (input.contains("error")) {
                            throw new RuntimeException("遇到错误关键字");
                        }
                        
                        return "Successfully processed: " + input;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("处理被中断", e);
                    }
                }, mixedThreadPool)
                .exceptionally(ex -> {
                    log.error("任务执行异常: {}", ex.getMessage());
                    return "Error processing: " + input + ", Reason: " + ex.getMessage();
                })
                .thenApplyAsync(result -> {
                    log.info("最终结果: {}", result);
                    return result;
                }, mixedThreadPool);
    }
}