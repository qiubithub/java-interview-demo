package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 无折扣策略实现
 */
public class NoDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        return Optional.ofNullable(originalPrice).orElse(BigDecimal.ZERO);
    }
}