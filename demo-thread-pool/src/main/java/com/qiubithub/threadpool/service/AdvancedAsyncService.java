package com.qiubithub.threadpool.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 高级异步服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface AdvancedAsyncService {

    /**
     * 并行处理列表数据
     *
     * @param dataList 数据列表
     * @return 处理结果
     */
    CompletableFuture<List<String>> processListParallel(List<String> dataList);

    /**
     * 链式处理任务
     *
     * @param input 输入数据
     * @return 处理结果
     */
    CompletableFuture<String> processChainedTasks(String input);

    /**
     * 组合多个异步任务
     *
     * @param param1 参数1
     * @param param2 参数2
     * @return 组合结果
     */
    CompletableFuture<String> combineAsyncTasks(String param1, String param2);

    /**
     * 异步任务异常处理
     *
     * @param input 输入数据
     * @return 处理结果
     */
    CompletableFuture<String> executeWithErrorHandling(String input);
}