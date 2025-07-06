package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * VIP会员折扣策略实现
 * 根据会员等级提供不同折扣
 */
public class VipDiscountStrategy implements DiscountStrategy {
    
    private final VipLevel vipLevel;
    
    /**
     * 构造函数
     * 
     * @param vipLevel VIP等级
     */
    public VipDiscountStrategy(VipLevel vipLevel) {
        this.vipLevel = vipLevel == null ? VipLevel.NORMAL : vipLevel;
    }
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 根据会员等级计算折扣
        BigDecimal discountRate = BigDecimal.ONE.subtract(vipLevel.getDiscountRate());
        return originalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * VIP等级枚举
     */
    public enum VipLevel {
        /**
         * 普通会员 - 无折扣
         */
        NORMAL(new BigDecimal("0.00")),
        
        /**
         * 银卡会员 - 5%折扣
         */
        SILVER(new BigDecimal("0.05")),
        
        /**
         * 金卡会员 - 10%折扣
         */
        GOLD(new BigDecimal("0.10")),
        
        /**
         * 钻石会员 - 15%折扣
         */
        DIAMOND(new BigDecimal("0.15")),
        
        /**
         * 黑金会员 - 20%折扣
         */
        BLACK(new BigDecimal("0.20"));
        
        private final BigDecimal discountRate;
        
        VipLevel(BigDecimal discountRate) {
            this.discountRate = discountRate;
        }
        
        public BigDecimal getDiscountRate() {
            return discountRate;
        }
    }
}