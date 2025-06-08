package com.qiubithub.designpattern.factory.method;

import com.qiubithub.designpattern.factory.simple.PaymentService;

/**
 * 工厂方法模式 - 支付工厂接口
 * 
 * 优点：
 * 1. 遵循开闭原则，新增产品只需添加对应工厂实现类
 * 2. 遵循单一职责原则，每个工厂只负责创建对应产品
 * 
 * 缺点：
 * 1. 类的个数容易过多，增加复杂度
 * 2. 增加了系统的抽象性和理解难度
 */
public interface PaymentFactory {
    
    /**
     * 创建支付服务
     * 
     * @return 支付服务实例
     */
    PaymentService createPayment();
} 