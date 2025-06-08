package com.qiubithub.designpattern.factory.abstracts;

/**
 * 抽象工厂模式 - 支付工厂接口
 * 
 * 优点：
 * 1. 分离了具体的产品和客户端
 * 2. 一个工厂可以创建多个产品族，而不仅仅是一个产品
 * 3. 增加新的产品族很方便，无需修改已有系统
 * 
 * 缺点：
 * 1. 增加新的产品等级结构麻烦，需要修改抽象工厂和所有的具体工厂类
 * 2. 产品族扩展困难
 */
public interface PaymentFactory {
    
    /**
     * 创建支付API
     * 
     * @return 支付API实例
     */
    PaymentApi createPaymentApi();
    
    /**
     * 创建退款API
     * 
     * @return 退款API实例
     */
    RefundApi createRefundApi();
} 