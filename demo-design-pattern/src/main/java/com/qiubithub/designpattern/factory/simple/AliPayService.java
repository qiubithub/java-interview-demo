package com.qiubithub.designpattern.factory.simple;

import java.math.BigDecimal;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝支付服务实现
 */
public class AliPayService implements PaymentService {
    private static final Logger logger = Logger.getLogger(AliPayService.class.getName());

    @Override
    public boolean pay(String orderId, BigDecimal amount) {
        if (StringUtils.isBlank(orderId) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        logger.info("使用支付宝完成支付，订单号：" + orderId + "，金额：" + amount);
        // 实际项目中这里会调用支付宝SDK进行支付
        return true;
    }
} 