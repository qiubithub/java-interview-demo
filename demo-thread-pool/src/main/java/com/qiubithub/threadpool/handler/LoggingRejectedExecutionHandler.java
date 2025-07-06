package com.qiubithub.threadpool.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 记录日志的拒绝策略处理器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
public class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {

    /**
     * 内部委托的拒绝策略
     */
    private final RejectedExecutionHandler delegate;

    /**
     * 构造函数，使用指定的拒绝策略
     *
     * @param delegate 委托的拒绝策略
     */
    public LoggingRejectedExecutionHandler(RejectedExecutionHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.warn("任务被拒绝执行，线程池状态: [poolSize={}, activeCount={}, queueSize={}]",
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getQueue().size());

        // 委托给实际的拒绝策略处理
        delegate.rejectedExecution(r, executor);
    }
}