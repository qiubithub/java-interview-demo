package com.qiubithub.designpattern.factory.abstracts;

import java.math.BigDecimal;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信支付API实现
 */
public class WeChatPayApi implements PaymentApi {
    private static final Logger logger = Logger.getLogger(WeChatPayApi.class.getName());
    
    @Override
    public boolean pay(String orderId, BigDecimal amount) {
        if (StringUtils.isBlank(orderId) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        logger.info("使用微信支付API完成支付，订单号：" + orderId + "，金额：" + amount);
        return true;
    }
} 