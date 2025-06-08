package com.qiubithub.designpattern.factory.abstracts;

/**
 * 支付宝支付工厂实现
 */
public class AliPayFactory implements PaymentFactory {
    
    @Override
    public com.qiubithub.designpattern.factory.abstracts.PaymentApi createPaymentApi() {
        return new com.qiubithub.designpattern.factory.abstracts.AliPayApi();
    }
    
    @Override
    public com.qiubithub.designpattern.factory.abstracts.RefundApi createRefundApi() {
        return new com.qiubithub.designpattern.factory.abstracts.AliRefundApi();
    }
} 