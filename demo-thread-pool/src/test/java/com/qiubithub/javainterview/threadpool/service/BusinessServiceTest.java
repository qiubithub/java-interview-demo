package com.qiubithub.javainterview.threadpool.service;

import com.qiubithub.threadpool.service.BusinessService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>
 * 电商会员业务服务测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest(classes = com.qiubithub.threadpool.ThreadPoolApplication.class)
public class BusinessServiceTest {

    @Autowired
    private BusinessService businessService;

    @Test
    @DisplayName("测试无锁版本的损失情况")
    void testWithoutLockLossAnalysis() throws InterruptedException {
        int threadCount = 10;
        String userId = "no-lock-user";
        
        System.out.println(businessService.runConcurrentTestWithoutLock(userId, threadCount));
    }

    @Test
    @DisplayName("测试分布式锁版本的保护效果")
    void testWithLockProtectionAnalysis() throws InterruptedException {
        int threadCount = 10;
        String userId = "lock-protected-user";
        
        System.out.println(businessService.runConcurrentTestWithLock(userId, threadCount));
    }

    @Test
    @DisplayName("对比分析：无锁 vs 分布式锁")
    void testComparisonAnalysis() throws InterruptedException {
        System.out.println(businessService.runComparisonTest(10));
    }
}