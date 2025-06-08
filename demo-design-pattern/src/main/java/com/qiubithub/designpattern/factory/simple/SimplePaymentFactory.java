package com.qiubithub.designpattern.factory.simple;

import java.util.Optional;

/**
 * 简单工厂模式 - 支付工厂
 * 
 * 优点：
 * 1. 将对象的创建和使用分离，客户端无需知道具体实现类
 * 2. 客户端只需要知道参数即可，无需知道创建逻辑
 * 
 * 缺点：
 * 1. 工厂类职责过重，违反单一职责原则
 * 2. 增加新产品需要修改工厂类代码，违反开闭原则
 * 3. 不易于扩展
 */
public class SimplePaymentFactory {
    
    /**
     * 根据支付类型创建对应的支付服务
     * 
     * @param type 支付类型
     * @return 支付服务实例
     */
    public static PaymentService createPayment(PaymentType type) {
        return Optional.ofNullable(type).map(payType -> {
            switch (payType) {
                case ALIPAY:
                    return new AliPayService();
                case WECHAT:
                    return new WeChatPayService();
                case UNIONPAY:
                    return new UnionPayService();
                default:
                    return null;
            }
        }).orElse(null);
    }
} 