package com.qiubithub.designpattern.factory.simple;

import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 支付操作
     * 
     * @param orderId 订单ID
     * @param amount 支付金额
     * @return 支付结果
     */
    boolean pay(String orderId, BigDecimal amount);
} 