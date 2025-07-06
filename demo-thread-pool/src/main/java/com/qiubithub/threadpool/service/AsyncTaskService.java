package com.qiubithub.threadpool.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * <p>
 * 异步任务服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface AsyncTaskService {

    /**
     * 执行IO密集型异步任务
     *
     * @param taskId 任务ID
     * @return 异步结果
     */
    CompletableFuture<String> executeIoTask(String taskId);

    /**
     * 执行CPU密集型异步任务
     *
     * @param taskId 任务ID
     * @return 异步结果
     */
    CompletableFuture<String> executeCpuTask(String taskId);

    /**
     * 执行混合型异步任务
     *
     * @param taskId 任务ID
     * @return 异步结果
     */
    CompletableFuture<String> executeMixedTask(String taskId);
}