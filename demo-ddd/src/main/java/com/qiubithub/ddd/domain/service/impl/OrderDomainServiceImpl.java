package com.qiubithub.ddd.domain.service.impl;

import com.qiubithub.ddd.domain.model.aggregate.Order;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;
import com.qiubithub.ddd.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;

/**
 * 订单领域服务实现类
 */
@Service
public class OrderDomainServiceImpl implements OrderDomainService {
    
    @Override
    public Order createOrder(String userId, Address shippingAddress, String recipientName, String recipientPhone) {
        return new Order(userId, shippingAddress, recipientName, recipientPhone);
    }
    
    @Override
    public Order addOrderItem(Order order, String productId, String productName, String productImage, Money unitPrice, int quantity) {
        order.addOrderItem(productId, productName, productImage, unitPrice, quantity);
        return order;
    }
    
    @Override
    public Order removeOrderItem(Order order, String orderItemId) {
        order.removeOrderItem(orderItemId);
        return order;
    }
    
    @Override
    public Order updateOrderItemQuantity(Order order, String orderItemId, int quantity) {
        order.updateOrderItemQuantity(orderItemId, quantity);
        return order;
    }
    
    @Override
    public Order payOrder(Order order) {
        order.pay();
        return order;
    }
    
    @Override
    public Order shipOrder(Order order) {
        order.ship();
        return order;
    }
    
    @Override
    public Order deliverOrder(Order order) {
        order.deliver();
        return order;
    }
    
    @Override
    public Order completeOrder(Order order) {
        order.complete();
        return order;
    }
    
    @Override
    public Order cancelOrder(Order order) {
        order.cancel();
        return order;
    }
    
    @Override
    public Order updateShippingAddress(Order order, Address shippingAddress) {
        order.updateShippingAddress(shippingAddress);
        return order;
    }
    
    @Override
    public Order updateRecipientInfo(Order order, String recipientName, String recipientPhone) {
        order.updateRecipientInfo(recipientName, recipientPhone);
        return order;
    }
}