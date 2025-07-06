package com.qiubithub.threadpool.controller;

import com.qiubithub.threadpool.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 并发测试控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@RestController
@RequestMapping("/api/concurrency-test")
public class ConcurrencyTestController {

    @Autowired
    private BusinessService businessService;

    /**
     * 执行无锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @GetMapping("/without-lock")
    public String testWithoutLock(@RequestParam(defaultValue = "test-user") String userId,
                                 @RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {
        return businessService.runConcurrentTestWithoutLock(userId, threadCount);
    }

    /**
     * 执行分布式锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @GetMapping("/with-lock")
    public String testWithLock(@RequestParam(defaultValue = "test-user") String userId,
                              @RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {
        return businessService.runConcurrentTestWithLock(userId, threadCount);
    }

    /**
     * 执行对比测试：无锁 vs 分布式锁
     *
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @GetMapping("/comparison")
    public String testComparison(@RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {
        return businessService.runComparisonTest(threadCount);
    }
    
    /**
     * 重置统计数据
     *
     * @return 结果
     */
    @PostMapping("/reset")
    public String resetStatistics() {
        businessService.resetLossStatistics();
        return "统计数据已重置";
    }
} 