package com.qiubithub.threadpool.service;

/**
 * <p>
 * 业务服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface BusinessService {

    /**
     * 使用分布式锁处理业务
     *
     * @param businessId 业务ID
     * @return 处理结果
     */
    String processBusinessWithLock(String businessId);

    /**
     * 不使用分布式锁处理业务
     *
     * @param businessId 业务ID
     * @return 处理结果
     */
    String processBusinessWithoutLock(String businessId);
}