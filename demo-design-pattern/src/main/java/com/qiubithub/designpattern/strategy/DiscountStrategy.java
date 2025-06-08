package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;

/**
 * 折扣策略接口
 * 
 * 策略模式定义了一系列算法，并将每个算法封装起来，使它们可以相互替换，
 * 且算法的变化不会影响使用算法的客户
 */
public interface DiscountStrategy {
    
    /**
     * 计算折扣后的价格
     * 
     * @param originalPrice 原始价格
     * @return 折扣后的价格
     */
    BigDecimal calculateDiscount(BigDecimal originalPrice);
}