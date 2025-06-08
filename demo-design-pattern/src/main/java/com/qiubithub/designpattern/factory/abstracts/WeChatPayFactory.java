package com.qiubithub.designpattern.factory.abstracts;

/**
 * 微信支付工厂实现
 */
public class WeChatPayFactory implements PaymentFactory {
    
    @Override
    public PaymentApi createPaymentApi() {
        return new WeChatPayApi();
    }
    
    @Override
    public RefundApi createRefundApi() {
        return new WeChatRefundApi();
    }
} 