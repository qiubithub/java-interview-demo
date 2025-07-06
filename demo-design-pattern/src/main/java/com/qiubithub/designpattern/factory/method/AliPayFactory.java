package com.qiubithub.designpattern.factory.method;

import com.qiubithub.designpattern.factory.simple.AliPayService;
import com.qiubithub.designpattern.factory.simple.PaymentService;

/**
 * 支付宝支付工厂实现
 */
public class AliPayFactory implements PaymentFactory {
    
    @Override
    public PaymentService createPayment() {
        return new AliPayService();
    }
} 