package com.qiubithub.threadpool.service;

/**
 * <p>
 * 定时任务服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface ScheduledTaskService {

    /**
     * 启动定时任务
     */
    void startScheduledTasks();

    /**
     * 停止定时任务
     */
    void stopScheduledTasks();

    /**
     * 获取定时任务状态
     *
     * @return 定时任务状态
     */
    boolean isRunning();
}