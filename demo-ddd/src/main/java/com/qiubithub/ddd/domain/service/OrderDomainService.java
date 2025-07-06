package com.qiubithub.ddd.domain.service;

import com.qiubithub.ddd.domain.model.aggregate.Order;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;

/**
 * 订单领域服务接口
 */
public interface OrderDomainService {
    
    /**
     * 创建订单
     *
     * @param userId          用户ID
     * @param shippingAddress 配送地址
     * @param recipientName   收件人姓名
     * @param recipientPhone  收件人电话
     * @return 创建的订单
     */
    Order createOrder(String userId, Address shippingAddress, String recipientName, String recipientPhone);
    
    /**
     * 向订单添加商品
     *
     * @param order        订单
     * @param productId    商品ID
     * @param productName  商品名称
     * @param productImage 商品图片
     * @param unitPrice    单价
     * @param quantity     数量
     * @return 更新后的订单
     */
    Order addOrderItem(Order order, String productId, String productName, String productImage, Money unitPrice, int quantity);
    
    /**
     * 从订单中移除商品
     *
     * @param order       订单
     * @param orderItemId 订单项ID
     * @return 更新后的订单
     */
    Order removeOrderItem(Order order, String orderItemId);
    
    /**
     * 更新订单中商品的数量
     *
     * @param order       订单
     * @param orderItemId 订单项ID
     * @param quantity    新数量
     * @return 更新后的订单
     */
    Order updateOrderItemQuantity(Order order, String orderItemId, int quantity);
    
    /**
     * 支付订单
     *
     * @param order 订单
     * @return 更新后的订单
     */
    Order payOrder(Order order);
    
    /**
     * 订单发货
     *
     * @param order 订单
     * @return 更新后的订单
     */
    Order shipOrder(Order order);
    
    /**
     * 确认订单送达
     *
     * @param order 订单
     * @return 更新后的订单
     */
    Order deliverOrder(Order order);
    
    /**
     * 完成订单
     *
     * @param order 订单
     * @return 更新后的订单
     */
    Order completeOrder(Order order);
    
    /**
     * 取消订单
     *
     * @param order 订单
     * @return 更新后的订单
     */
    Order cancelOrder(Order order);
    
    /**
     * 更新订单收货地址
     *
     * @param order          订单
     * @param shippingAddress 新的收货地址
     * @return 更新后的订单
     */
    Order updateShippingAddress(Order order, Address shippingAddress);
    
    /**
     * 更新订单收件人信息
     *
     * @param order          订单
     * @param recipientName  收件人姓名
     * @param recipientPhone 收件人电话
     * @return 更新后的订单
     */
    Order updateRecipientInfo(Order order, String recipientName, String recipientPhone);
}