package com.qiubithub.threadpool.service.impl;

import com.qiubithub.threadpool.annotation.DistributedLockable;
import com.qiubithub.threadpool.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 电商会员业务服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class BusinessServiceImpl implements BusinessService {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    // 会员价格配置
    private static final BigDecimal MEMBERSHIP_PRICE = new BigDecimal("99.00"); // 会员价格99元
    private static final BigDecimal THIRD_PARTY_COST_RATE = new BigDecimal("0.60"); // 第三方成本60%
    private static final BigDecimal THIRD_PARTY_COST = MEMBERSHIP_PRICE.multiply(THIRD_PARTY_COST_RATE); // 成本59.4元
    
    // 损失统计
    private final AtomicInteger totalPurchaseAttempts = new AtomicInteger(0); // 总购买尝试次数
    private final AtomicInteger successfulPurchases = new AtomicInteger(0); // 成功购买次数
    private final AtomicInteger duplicatePurchases = new AtomicInteger(0); // 重复购买次数
    private final AtomicLong totalRevenueLoss = new AtomicLong(0); // 总收入损失（分）
    private final AtomicLong totalCostLoss = new AtomicLong(0); // 总成本损失（分）
    private final ConcurrentHashMap<String, Boolean> userMembershipStatus = new ConcurrentHashMap<>(); // 用户会员状态
    
    @Autowired
    @Qualifier("ioThreadPool")
    private Executor ioThreadPool; // 注入IO线程池

    @Override
    @DistributedLockable(
        prefix = "membership", 
        key = "#userId", 
        leaseTime = -1,  // 使用看门狗机制自动续期
        waitTime = 5, 
        timeUnit = TimeUnit.SECONDS,
        lockType = DistributedLockable.LockType.REENTRANT_LOCK,
        failFast = true
    )
    public String purchaseMembershipWithLock(String userId) {
        totalPurchaseAttempts.incrementAndGet();
        String startTime = LocalDateTime.now().format(formatter);
        log.info("【加锁】用户 {} 开始购买会员, 线程: {}, 开始时间: {}", 
                userId, Thread.currentThread().getName(), startTime);
        
        try {
            // 1. 检查用户是否已经是会员
            Thread.sleep(500);
            Boolean isMember = userMembershipStatus.get(userId);
            if (Boolean.TRUE.equals(isMember)) {
                log.warn("【加锁】用户 {} 已经是会员，拒绝重复购买", userId);
                return "用户 " + userId + " 已经是会员，无需重复购买";
            }
            log.info("【加锁】检查用户 {} 会员状态完成，非会员可以购买", userId);
            
            // 2. 验证支付信息
            Thread.sleep(800);
            log.info("【加锁】验证用户 {} 支付信息完成", userId);
            
            // 3. 调用第三方平台激活会员（模拟成本产生）
            Thread.sleep(600);
            log.info("【加锁】调用第三方平台为用户 {} 激活会员，成本: {}元", userId, THIRD_PARTY_COST);
            
            // 4. 扣减账户余额并更新会员状态
            Thread.sleep(700);
            userMembershipStatus.put(userId, true);
            successfulPurchases.incrementAndGet();
            log.info("【加锁】用户 {} 购买会员成功，收入: {}元，成本: {}元，利润: {}元", 
                    userId, MEMBERSHIP_PRICE, THIRD_PARTY_COST, 
                    MEMBERSHIP_PRICE.subtract(THIRD_PARTY_COST));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("用户 {} 购买会员被中断", userId, e);
            return "用户 " + userId + " 购买会员失败: " + e.getMessage();
        }
        
        String endTime = LocalDateTime.now().format(formatter);
        log.info("【加锁】用户 {} 购买会员完成, 线程: {}, 结束时间: {}", 
                userId, Thread.currentThread().getName(), endTime);
        return "用户 " + userId + " 购买会员成功（使用分布式锁防止重复购买）";
    }

    @Override
    public String purchaseMembershipWithoutLock(String userId) {
        totalPurchaseAttempts.incrementAndGet();
        String startTime = LocalDateTime.now().format(formatter);
        log.info("【无锁】用户 {} 开始购买会员, 线程: {}, 开始时间: {}", 
                userId, Thread.currentThread().getName(), startTime);
        
        try {
            // 1. 检查用户是否已经是会员（无锁情况下可能有并发问题）
            Thread.sleep(500);
            Boolean isMember = userMembershipStatus.get(userId);
            if (Boolean.TRUE.equals(isMember)) {
                // 无锁情况下，可能多个线程同时通过了检查，导致重复购买
                log.warn("【无锁】用户 {} 检查时发现已是会员，但继续处理（模拟并发问题）", userId);
            } else {
                log.info("【无锁】检查用户 {} 会员状态完成，非会员可以购买", userId);
            }
            
            // 2. 验证支付信息
            Thread.sleep(800);
            log.info("【无锁】验证用户 {} 支付信息完成", userId);
            
            // 3. 调用第三方平台激活会员（产生成本）
            Thread.sleep(600);
            log.info("【无锁】调用第三方平台为用户 {} 激活会员，成本: {}元", userId, THIRD_PARTY_COST);
            
            // 4. 扣减账户余额并更新会员状态（无锁可能导致重复操作）
            Thread.sleep(700);
            Boolean previousStatus = userMembershipStatus.put(userId, true);
            
            if (Boolean.TRUE.equals(previousStatus)) {
                // 检测到重复购买
                duplicatePurchases.incrementAndGet();
                log.error("【无锁】用户 {} 重复购买会员！产生额外成本: {}元", userId, THIRD_PARTY_COST);
                return "用户 " + userId + " 重复购买会员（产生额外成本损失）";
            } else {
                successfulPurchases.incrementAndGet();
                log.info("【无锁】用户 {} 购买会员成功，收入: {}元，成本: {}元，利润: {}元", 
                        userId, MEMBERSHIP_PRICE, THIRD_PARTY_COST, 
                        MEMBERSHIP_PRICE.subtract(THIRD_PARTY_COST));
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("用户 {} 购买会员被中断", userId, e);
            return "用户 " + userId + " 购买会员失败: " + e.getMessage();
        }
        
        String endTime = LocalDateTime.now().format(formatter);
        log.info("【无锁】用户 {} 购买会员完成, 线程: {}, 结束时间: {}", 
                userId, Thread.currentThread().getName(), endTime);
        return "用户 " + userId + " 购买会员成功（无锁可能导致重复购买）";
    }

    @Override
    public String getLossStatistics() {
        int attempts = totalPurchaseAttempts.get();
        int successful = successfulPurchases.get();
        int duplicates = duplicatePurchases.get();
        
        BigDecimal totalRevenue = MEMBERSHIP_PRICE.multiply(new BigDecimal(successful));
        BigDecimal totalCost = THIRD_PARTY_COST.multiply(new BigDecimal(successful + duplicates));
        BigDecimal actualProfit = totalRevenue.subtract(THIRD_PARTY_COST.multiply(new BigDecimal(successful)));
        BigDecimal lossFromDuplicates = THIRD_PARTY_COST.multiply(new BigDecimal(duplicates));
        
        StringBuilder report = new StringBuilder();
        report.append("\n=== 电商会员业务损失统计报告 ===\n");
        report.append("总购买尝试次数: ").append(attempts).append("\n");
        report.append("成功购买次数: ").append(successful).append("\n");
        report.append("重复购买次数: ").append(duplicates).append("\n");
        report.append("重复购买率: ").append(attempts > 0 ? String.format("%.2f%%", (double) duplicates / attempts * 100) : "0%").append("\n");
        report.append("单价: ").append(MEMBERSHIP_PRICE).append("元\n");
        report.append("第三方成本: ").append(THIRD_PARTY_COST).append("元 (").append(THIRD_PARTY_COST_RATE.multiply(new BigDecimal(100))).append("%)\n");
        report.append("总收入: ").append(totalRevenue).append("元\n");
        report.append("总成本: ").append(totalCost).append("元\n");
        report.append("实际利润: ").append(actualProfit).append("元\n");
        report.append("重复购买损失: ").append(lossFromDuplicates).append("元\n");
        report.append("损失率: ").append(totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                String.format("%.2f%%", lossFromDuplicates.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue()) : "0%").append("\n");
        report.append("===============================");
        
        return report.toString();
    }

    @Override
    public void resetLossStatistics() {
        totalPurchaseAttempts.set(0);
        successfulPurchases.set(0);
        duplicatePurchases.set(0);
        totalRevenueLoss.set(0);
        totalCostLoss.set(0);
        userMembershipStatus.clear();
        log.info("损失统计已重置");
    }

    /**
     * 执行无锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @Override
    public String runConcurrentTestWithoutLock(String userId, int threadCount) throws InterruptedException {
        // 重置统计
        resetLossStatistics();
        
        StringBuilder result = new StringBuilder();
        result.append("=== 无锁版本损失测试 ===\n");
        result.append("模拟场景：").append(threadCount).append(" 个线程同时为用户 ").append(userId).append(" 购买会员\n");
        result.append("会员价格：99元，第三方平台成本：59.4元（60%），预期利润：39.6元\n");
        result.append("风险：无分布式锁保护，可能产生重复购买\n");
        
        long startTime = System.currentTimeMillis();
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            ioThreadPool.execute(() -> {
                try {
                    purchaseMembershipWithoutLock(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        result.append("\n--- 无锁版本测试结果 ---\n");
        result.append("总耗时：").append(totalTime).append("ms\n");
        result.append(getLossStatistics()).append("\n");
        
        result.append("\n--- 无锁版本风险分析 ---\n");
        result.append("⚠️  高并发下容易产生重复购买\n");
        result.append("⚠️  每次重复购买损失59.4元第三方成本\n");
        result.append("⚠️  损失率可能达到数百%\n");
        result.append("⚠️  资金安全风险极高");
        
        return result.toString();
    }
    
    /**
     * 执行分布式锁版本的高并发测试
     *
     * @param userId 用户ID
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @Override
    public String runConcurrentTestWithLock(String userId, int threadCount) throws InterruptedException {
        // 重置统计
        resetLossStatistics();
        
        StringBuilder result = new StringBuilder();
        result.append("=== 分布式锁保护版本测试 ===\n");
        result.append("模拟场景：").append(threadCount).append(" 个线程同时为用户 ").append(userId).append(" 购买会员\n");
        result.append("会员价格：99元，第三方平台成本：59.4元（60%），预期利润：39.6元\n");
        result.append("保护：使用分布式锁防止重复购买\n");
        
        long startTime = System.currentTimeMillis();
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            ioThreadPool.execute(() -> {
                try {
                    purchaseMembershipWithLock(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(60, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        result.append("\n--- 分布式锁版本测试结果 ---\n");
        result.append("总耗时：").append(totalTime).append("ms\n");
        result.append(getLossStatistics()).append("\n");
        
        result.append("\n--- 分布式锁保护效果 ---\n");
        result.append("✅ 完全避免重复购买\n");
        result.append("✅ 零额外成本损失\n");
        result.append("✅ 保护业务利润\n");
        result.append("✅ 资金安全有保障\n");
        result.append("⚠️  耗时略有增加（锁竞争开销）");
        
        return result.toString();
    }
    
    /**
     * 执行对比测试：无锁 vs 分布式锁
     *
     * @param threadCount 并发线程数
     * @return 测试结果
     * @throws InterruptedException 线程中断异常
     */
    @Override
    public String runComparisonTest(int threadCount) throws InterruptedException {
        StringBuilder result = new StringBuilder();
        result.append("=== 无锁 vs 分布式锁 对比分析 ===\n");
        result.append("测试场景：").append(threadCount).append("个线程并发购买同一用户的会员\n");
        result.append("业务参数：会员价格99元，第三方成本59.4元（60%）\n");
        
        // 先测试无锁版本
        result.append("\n🔴 第一轮：无锁版本测试\n");
        result.append(runConcurrentTestWithoutLock("no-lock-user", threadCount));
        
        Thread.sleep(2000); // 等待2秒
        
        // 再测试加锁版本
        result.append("\n\n🟢 第二轮：分布式锁版本测试\n");
        result.append(runConcurrentTestWithLock("lock-protected-user", threadCount));
        
        result.append("\n\n📊 对比总结\n");
        result.append("┌─────────────────┬─────────────┬─────────────────┐\n");
        result.append("│     版本        │   无锁版本   │   分布式锁版本   │\n");
        result.append("├─────────────────┼─────────────┼─────────────────┤\n");
        result.append("│ 重复购买风险     │     极高     │       无        │\n");
        result.append("│ 成本损失风险     │   数百元     │       0元       │\n");
        result.append("│ 资金安全性       │     差       │      优秀       │\n");
        result.append("│ 性能开销         │     低       │     略高        │\n");
        result.append("│ 适用场景         │   非关键     │   关键业务      │\n");
        result.append("└─────────────────┴─────────────┴─────────────────┘\n");
        
        result.append("\n💡 建议：\n");
        result.append("• 涉及第三方付费服务时，必须使用分布式锁\n");
        result.append("• 会员购买、支付等关键业务，安全性优先于性能\n");
        result.append("• 可以接受少量性能损失来换取资金安全");
        
        return result.toString();
    }
} 