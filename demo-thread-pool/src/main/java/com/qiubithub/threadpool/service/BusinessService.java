package com.qiubithub.threadpool.service;

/**
 * <p>
 * 电商会员业务服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface BusinessService {

    /**
     * 使用分布式锁处理用户购买会员业务
     *
     * @param userId 用户ID
     * @return 处理结果
     */
    String purchaseMembershipWithLock(String userId);

    /**
     * 不使用分布式锁处理用户购买会员业务
     *
     * @param userId 用户ID
     * @return 处理结果
     */
    String purchaseMembershipWithoutLock(String userId);

    /**
     * 获取损失统计信息
     *
     * @return 损失统计结果
     */
    String getLossStatistics();

    /**
     * 重置损失统计
     */
    void resetLossStatistics();
    
    /**
     * 执行无锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    String runConcurrentTestWithoutLock(String userId, int threadCount) throws InterruptedException;
    
    /**
     * 执行分布式锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    String runConcurrentTestWithLock(String userId, int threadCount) throws InterruptedException;
    
    /**
     * 执行对比测试：无锁 vs 分布式锁
     *
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    String runComparisonTest(int threadCount) throws InterruptedException;
}