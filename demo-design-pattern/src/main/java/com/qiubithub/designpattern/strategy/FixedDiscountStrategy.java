package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 固定金额折扣策略实现
 */
public class FixedDiscountStrategy implements DiscountStrategy {
    
    private final BigDecimal discountAmount;
    
    /**
     * 构造函数
     * 
     * @param discountAmount 折扣金额
     */
    public FixedDiscountStrategy(BigDecimal discountAmount) {
        this.discountAmount = Optional.ofNullable(discountAmount).orElse(BigDecimal.ZERO);
    }
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);
        return finalPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalPrice;
    }
}