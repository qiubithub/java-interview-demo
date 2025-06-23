package com.qiubithub.ddd.domain.repository;

import com.qiubithub.ddd.domain.model.aggregate.Order;

import java.util.List;
import java.util.Optional;

/**
 * 订单领域仓储接口
 */
public interface OrderRepository {
    
    /**
     * 保存订单
     *
     * @param order 订单
     * @return 保存后的订单
     */
    Order save(Order order);
    
    /**
     * 根据ID查找订单
     *
     * @param id 订单ID
     * @return 订单
     */
    Optional<Order> findById(String id);
    
    /**
     * 根据订单编号查找订单
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 查询用户的所有订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(String userId);
    
    /**
     * 查询用户的特定状态订单
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
    
    /**
     * 删除订单
     *
     * @param id 订单ID
     */
    void deleteById(String id);
}