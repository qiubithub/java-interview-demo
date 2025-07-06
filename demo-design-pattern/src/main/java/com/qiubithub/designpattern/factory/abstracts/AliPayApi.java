package com.qiubithub.designpattern.factory.abstracts;

import java.math.BigDecimal;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝API实现
 */
public class AliPayApi implements PaymentApi {
    private static final Logger logger = Logger.getLogger(AliPayApi.class.getName());
    
    @Override
    public boolean pay(String orderId, BigDecimal amount) {
        if (StringUtils.isBlank(orderId) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        logger.info("使用支付宝API完成 支付，订单号：" + orderId + "，金额：" + amount);
        return true;
    }
} 