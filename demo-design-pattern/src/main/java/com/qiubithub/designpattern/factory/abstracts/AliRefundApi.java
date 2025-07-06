package com.qiubithub.designpattern.factory.abstracts;

import java.math.BigDecimal;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝退款API实现
 */
public class AliRefundApi implements RefundApi {
    private static final Logger logger = Logger.getLogger(AliRefundApi.class.getName());
    
    @Override
    public boolean refund(String orderId, BigDecimal amount) {
        if (StringUtils.isBlank(orderId) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        logger.info("使用支付宝API完成退款，订单号：" + orderId + "，金额：" + amount);
        return true;
    }
} 