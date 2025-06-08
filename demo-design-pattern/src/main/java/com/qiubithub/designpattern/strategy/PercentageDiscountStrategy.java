package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * 百分比折扣策略实现
 */
public class PercentageDiscountStrategy implements DiscountStrategy {
    
    private final BigDecimal discountPercentage;
    
    /**
     * 构造函数
     * 
     * @param discountPercentage 折扣百分比（0-100）
     */
    public PercentageDiscountStrategy(BigDecimal discountPercentage) {
        // 确保折扣百分比在0-100之间
        BigDecimal percentage = Optional.ofNullable(discountPercentage).orElse(BigDecimal.ZERO);
        if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            percentage = BigDecimal.ZERO;
        } else if (percentage.compareTo(new BigDecimal("100")) > 0) {
            percentage = new BigDecimal("100");
        }
        this.discountPercentage = percentage;
    }
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 计算折扣后的价格：原价 * (1 - 折扣百分比/100)
        BigDecimal discountRate = BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        return originalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
    }
}