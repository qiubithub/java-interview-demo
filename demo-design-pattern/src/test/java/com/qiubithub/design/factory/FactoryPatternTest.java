package com.qiubithub.designpattern.factory;

import com.qiubithub.designpattern.factory.abstracts.AliPayFactory;
import com.qiubithub.designpattern.factory.abstracts.PaymentApi;
import com.qiubithub.designpattern.factory.abstracts.RefundApi;
import com.qiubithub.designpattern.factory.abstracts.WeChatPayFactory;
import com.qiubithub.designpattern.factory.method.PaymentFactory;
import com.qiubithub.designpattern.factory.simple.PaymentService;
import com.qiubithub.designpattern.factory.simple.PaymentType;
import com.qiubithub.designpattern.factory.simple.SimplePaymentFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 工厂模式测试类
 */
public class FactoryPatternTest {
    
    @Test
    @DisplayName("测试简单工厂模式")
    public void testSimpleFactory() {
        // 使用简单工厂创建支付宝支付服务
        PaymentService aliPayService = SimplePaymentFactory.createPayment(PaymentType.ALIPAY);
        assertNotNull(aliPayService, "支付宝支付服务不应为空");
        assertTrue(aliPayService.pay("ORDER123", new BigDecimal("100")), "支付宝支付应成功");
        
        // 使用简单工厂创建微信支付服务
        PaymentService wechatPayService = SimplePaymentFactory.createPayment(PaymentType.WECHAT);
        assertNotNull(wechatPayService, "微信支付服务不应为空");
        assertTrue(wechatPayService.pay("ORDER456", new BigDecimal("200")), "微信支付应成功");
        
        // 使用简单工厂创建银联支付服务
        PaymentService unionPayService = SimplePaymentFactory.createPayment(PaymentType.UNIONPAY);
        assertNotNull(unionPayService, "银联支付服务不应为空");
        assertTrue(unionPayService.pay("ORDER789", new BigDecimal("300")), "银联支付应成功");
    }    
    @Test
    @DisplayName("测试工厂方法模式")
    public void testFactoryMethod() {
        // 使用支付宝工厂创建支付服务
        PaymentFactory aliPayFactory = new com.qiubithub.designpattern.factory.method.AliPayFactory();
        PaymentService aliPayService = aliPayFactory.createPayment();
        assertNotNull(aliPayService, "支付宝支付服务不应为空");
        assertTrue(aliPayService.pay("ORDER123", new BigDecimal("100")), "支付宝支付应成功");
        
        // 使用微信支付工厂创建支付服务
        PaymentFactory wechatPayFactory = new com.qiubithub.designpattern.factory.method.WeChatPayFactory();
        PaymentService wechatPayService = wechatPayFactory.createPayment();
        assertNotNull(wechatPayService, "微信支付服务不应为空");
        assertTrue(wechatPayService.pay("ORDER456", new BigDecimal("200")), "微信支付应成功");
        
        // 使用银联支付工厂创建支付服务
        PaymentFactory unionPayFactory = new com.qiubithub.designpattern.factory.method.UnionPayFactory();
        PaymentService unionPayService = unionPayFactory.createPayment();
        assertNotNull(unionPayService, "银联支付服务不应为空");
        assertTrue(unionPayService.pay("ORDER789", new BigDecimal("300")), "银联支付应成功");
    }    
    @Test
    @DisplayName("测试抽象工厂模式")
    public void testAbstractFactory() {
        // 使用支付宝抽象工厂
        com.qiubithub.designpattern.factory.abstracts.PaymentFactory aliFactory = new AliPayFactory();
        
        // 创建支付宝支付API
        PaymentApi aliPayApi = aliFactory.createPaymentApi();
        assertNotNull(aliPayApi, "支付宝支付API不应为空");
        assertTrue(aliPayApi.pay("ORDER123", new BigDecimal("100")), "支付宝支付应成功");
        
        // 创建支付宝退款API
        RefundApi aliRefundApi = aliFactory.createRefundApi();
        assertNotNull(aliRefundApi, "支付宝退款API不应为空");
        assertTrue(aliRefundApi.refund("ORDER123", new BigDecimal("100")), "支付宝退款应成功");
        
        // 使用微信支付抽象工厂
        com.qiubithub.designpattern.factory.abstracts.PaymentFactory wechatFactory = new WeChatPayFactory();
        
        // 创建微信支付API
        PaymentApi wechatPayApi = wechatFactory.createPaymentApi();
        assertNotNull(wechatPayApi, "微信支付API不应为空");
        assertTrue(wechatPayApi.pay("ORDER456", new BigDecimal("200")), "微信支付应成功");
        
        // 创建微信退款API
        RefundApi wechatRefundApi = wechatFactory.createRefundApi();
        assertNotNull(wechatRefundApi, "微信退款API不应为空");
        assertTrue(wechatRefundApi.refund("ORDER456", new BigDecimal("200")), "微信退款应成功");
    }
}