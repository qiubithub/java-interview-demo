package com.qiubithub.designpattern.factory.abstracts;

import java.math.BigDecimal;

/**
 * 支付API接口
 */
public interface PaymentApi {
    
    /**
     * 支付接口
     * 
     * @param orderId 订单ID
     * @param amount 支付金额
     * @return 支付结果
     */
    boolean pay(String orderId, BigDecimal amount);
} 