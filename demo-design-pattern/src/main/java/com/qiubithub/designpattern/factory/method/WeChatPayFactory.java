package com.qiubithub.designpattern.factory.method;

import com.qiubithub.designpattern.factory.simple.PaymentService;
import com.qiubithub.designpattern.factory.simple.WeChatPayService;

/**
 * 微信支付工厂实现
 */
public class WeChatPayFactory implements PaymentFactory {
    
    @Override
    public PaymentService createPayment() {
        return new WeChatPayService();
    }
} 