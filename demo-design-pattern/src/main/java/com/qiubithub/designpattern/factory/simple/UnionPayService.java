package com.qiubithub.designpattern.factory.simple;

import java.math.BigDecimal;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 银联支付服务实现
 */
public class UnionPayService implements PaymentService {
    private static final Logger logger = Logger.getLogger(UnionPayService.class.getName());

    @Override
    public boolean pay(String orderId, BigDecimal amount) {
        if (StringUtils.isBlank(orderId) || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        logger.info("使用银联支付完成支付，订单号：" + orderId + "，金额：" + amount);
        // 实际项目中这里会调用银联SDK进行支付
        return true;
    }
} 