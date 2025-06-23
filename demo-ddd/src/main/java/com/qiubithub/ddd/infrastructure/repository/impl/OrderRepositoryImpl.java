package com.qiubithub.ddd.infrastructure.repository.impl;

import com.qiubithub.ddd.domain.model.aggregate.Order;
import com.qiubithub.ddd.domain.model.entity.OrderItem;
import com.qiubithub.ddd.domain.repository.OrderRepository;
import com.qiubithub.ddd.infrastructure.mapper.OrderItemMapper;
import com.qiubithub.ddd.infrastructure.mapper.OrderMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口实现
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    
    public OrderRepositoryImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }
    
    @Override
    @Transactional
    public Order save(Order order) {
        // 检查是否为新订单
        Order existingOrder = orderMapper.selectById(order.getId());
        
        if (existingOrder == null) {
            // 新订单，插入
            orderMapper.insert(order);
            
            // 插入订单项
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                orderItemMapper.batchInsert(new ArrayList<>(order.getOrderItems()));
            }
        } else {
            // 更新订单
            orderMapper.update(order);
            
            // 先删除旧的订单项
            orderItemMapper.deleteByOrderId(order.getId());
            
            // 再插入新的订单项
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                orderItemMapper.batchInsert(new ArrayList<>(order.getOrderItems()));
            }
        }
        
        return order;
    }
    
    @Override
    public Optional<Order> findById(String id) {
        Order order = orderMapper.selectById(id);
        return Optional.ofNullable(order);
    }
    
    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        Order order = orderMapper.selectByOrderNumber(orderNumber);
        return Optional.ofNullable(order);
    }
    
    @Override
    public List<Order> findByUserId(String userId) {
        return orderMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status) {
        return orderMapper.selectByUserIdAndStatus(userId, status.name());
    }
    
    @Override
    @Transactional
    public void deleteById(String id) {
        // 先删除订单项
        orderItemMapper.deleteByOrderId(id);
        // 再删除订单
        orderMapper.deleteById(id);
    }
}