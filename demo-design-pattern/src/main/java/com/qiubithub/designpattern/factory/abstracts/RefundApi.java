package com.qiubithub.designpattern.factory.abstracts;

import java.math.BigDecimal;

/**
 * 退款API接口
 */
public interface RefundApi {
    
    /**
     * 退款接口
     * 
     * @param orderId 订单ID
     * @param amount 退款金额
     * @return 退款结果
     */
    boolean refund(String orderId, BigDecimal amount);
} 