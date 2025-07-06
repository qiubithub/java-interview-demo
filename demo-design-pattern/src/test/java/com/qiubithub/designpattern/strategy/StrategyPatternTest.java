package com.qiubithub.designpattern.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 策略模式测试类
 */
public class StrategyPatternTest {
    
    @Test
    @DisplayName("测试无折扣策略")
    public void testNoDiscountStrategy() {
        DiscountStrategy strategy = new NoDiscountStrategy();
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = strategy.calculateDiscount(originalPrice);
        
        assertEquals(0, originalPrice.compareTo(discountedPrice), "无折扣策略应返回原价");
    }    
    @Test
    @DisplayName("测试固定金额折扣策略")
    public void testFixedDiscountStrategy() {
        BigDecimal discountAmount = new BigDecimal("20.00");
        DiscountStrategy strategy = new FixedDiscountStrategy(discountAmount);
        
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal expectedPrice = new BigDecimal("80.00");
        BigDecimal discountedPrice = strategy.calculateDiscount(originalPrice);
        
        assertEquals(0, expectedPrice.compareTo(discountedPrice), "固定金额折扣策略应减去指定金额");
        
        // 测试折扣金额大于原价的情况
        BigDecimal smallPrice = new BigDecimal("10.00");
        BigDecimal discountedSmallPrice = strategy.calculateDiscount(smallPrice);
        assertEquals(0, BigDecimal.ZERO.compareTo(discountedSmallPrice), "折扣后价格不应小于0");
    }
    
    @Test
    @DisplayName("测试百分比折扣策略")
    public void testPercentageDiscountStrategy() {
        BigDecimal discountPercentage = new BigDecimal("20"); // 20% 折扣
        DiscountStrategy strategy = new PercentageDiscountStrategy(discountPercentage);
        
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal expectedPrice = new BigDecimal("80.00");
        BigDecimal discountedPrice = strategy.calculateDiscount(originalPrice);
        
        assertEquals(0, expectedPrice.compareTo(discountedPrice), "百分比折扣策略应减去指定百分比");
        
        // 测试百分比超过100%的情况
        DiscountStrategy invalidStrategy = new PercentageDiscountStrategy(new BigDecimal("120"));
        BigDecimal invalidDiscountedPrice = invalidStrategy.calculateDiscount(originalPrice);
        assertEquals(0, BigDecimal.ZERO.compareTo(invalidDiscountedPrice), "百分比超过100%应返回0");
    }    
    @Test
    @DisplayName("测试VIP会员折扣策略")
    public void testVipDiscountStrategy() {
        // 测试金卡会员 (10% 折扣)
        DiscountStrategy goldStrategy = new VipDiscountStrategy(VipDiscountStrategy.VipLevel.GOLD);
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal expectedGoldPrice = new BigDecimal("90.00");
        BigDecimal goldDiscountedPrice = goldStrategy.calculateDiscount(originalPrice);
        
        assertEquals(0, expectedGoldPrice.compareTo(goldDiscountedPrice), "金卡会员应有10%折扣");
        
        // 测试钻石会员 (15% 折扣)
        DiscountStrategy diamondStrategy = new VipDiscountStrategy(VipDiscountStrategy.VipLevel.DIAMOND);
        BigDecimal expectedDiamondPrice = new BigDecimal("85.00");
        BigDecimal diamondDiscountedPrice = diamondStrategy.calculateDiscount(originalPrice);
        
        assertEquals(0, expectedDiamondPrice.compareTo(diamondDiscountedPrice), "钻石会员应有15%折扣");
    }
    
    @Test
    @DisplayName("测试折扣策略上下文")
    public void testDiscountContext() {
        BigDecimal originalPrice = new BigDecimal("100.00");
        
        // 创建上下文并设置初始策略
        DiscountContext context = new DiscountContext(new NoDiscountStrategy());
        BigDecimal price1 = context.executeStrategy(originalPrice);
        assertEquals(0, originalPrice.compareTo(price1), "无折扣策略应返回原价");
        
        // 切换到固定金额折扣策略
        context.setDiscountStrategy(new FixedDiscountStrategy(new BigDecimal("20.00")));
        BigDecimal price2 = context.executeStrategy(originalPrice);
        assertEquals(0, new BigDecimal("80.00").compareTo(price2), "固定金额折扣策略应减去指定金额");
        
        // 切换到百分比折扣策略
        context.setDiscountStrategy(new PercentageDiscountStrategy(new BigDecimal("30")));
        BigDecimal price3 = context.executeStrategy(originalPrice);
        assertEquals(0, new BigDecimal("70.00").compareTo(price3), "百分比折扣策略应减去指定百分比");
        
        // 切换到VIP会员折扣策略
        context.setDiscountStrategy(new VipDiscountStrategy(VipDiscountStrategy.VipLevel.BLACK));
        BigDecimal price4 = context.executeStrategy(originalPrice);
        assertEquals(0, new BigDecimal("80.00").compareTo(price4), "黑金会员应有20%折扣");
    }
}