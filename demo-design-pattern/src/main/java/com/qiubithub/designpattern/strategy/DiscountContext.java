package com.qiubithub.designpattern.strategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 折扣策略上下文类
 * 
 * 策略模式的优点：
 * 1. 算法可以自由切换
 * 2. 避免使用多重条件判断
 * 3. 扩展性良好，增加新的策略只需实现接口
 * 
 * 策略模式的缺点：
 * 1. 客户端必须知道所有的策略类，并自行决定使用哪一个策略类
 * 2. 策略模式会造成很多的策略类
 */
public class DiscountContext {
    
    private DiscountStrategy discountStrategy;
    
    /**
     * 构造函数
     * 
     * @param discountStrategy 折扣策略
     */
    public DiscountContext(DiscountStrategy discountStrategy) {
        this.discountStrategy = Optional.ofNullable(discountStrategy).orElse(new NoDiscountStrategy());
    }
    
    /**
     * 设置折扣策略
     * 
     * @param discountStrategy 折扣策略
     */
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = Optional.ofNullable(discountStrategy).orElse(new NoDiscountStrategy());
    }
    
    /**
     * 计算折扣
     * 
     * @param originalPrice 原始价格
     * @return 折扣后的价格
     */
    public BigDecimal executeStrategy(BigDecimal originalPrice) {
        return discountStrategy.calculateDiscount(originalPrice);
    }
}