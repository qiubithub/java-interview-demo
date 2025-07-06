package com.qiubithub.designpattern.factory.method;

import com.qiubithub.designpattern.factory.simple.PaymentService;
import com.qiubithub.designpattern.factory.simple.UnionPayService;

/**
 * 银联支付工厂实现
 */
public class UnionPayFactory implements PaymentFactory {
    
    @Override
    public PaymentService createPayment() {
        return new UnionPayService();
    }
} 