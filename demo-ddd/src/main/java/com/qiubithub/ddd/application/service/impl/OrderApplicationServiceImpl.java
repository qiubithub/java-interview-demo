package com.qiubithub.ddd.application.service.impl;

import com.qiubithub.ddd.application.dto.AddressDTO;
import com.qiubithub.ddd.application.dto.CreateOrderDTO;
import com.qiubithub.ddd.application.dto.OrderDTO;
import com.qiubithub.ddd.application.dto.OrderItemDTO;
import com.qiubithub.ddd.application.mapper.OrderMapper;
import com.qiubithub.ddd.application.service.OrderApplicationService;
import com.qiubithub.ddd.domain.model.aggregate.Order;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;
import com.qiubithub.ddd.domain.repository.OrderRepository;
import com.qiubithub.ddd.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 订单应用服务实现类
 */
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OrderMapper orderMapper;
    
    public OrderApplicationServiceImpl(OrderRepository orderRepository, OrderDomainService orderDomainService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.orderMapper = orderMapper;
    }
    
    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO createOrderDTO) {
        // 转换地址
        Address shippingAddress = orderMapper.toAddress(createOrderDTO.getShippingAddress());
        
        // 创建订单
        Order order = orderDomainService.createOrder(
                createOrderDTO.getUserId(),
                shippingAddress,
                createOrderDTO.getRecipientName(),
                createOrderDTO.getRecipientPhone()
        );
        
        // 添加订单项
        for (OrderItemDTO itemDTO : createOrderDTO.getOrderItems()) {
            Money unitPrice = orderMapper.toMoney(itemDTO.getUnitPrice(), itemDTO.getCurrencyCode());
            orderDomainService.addOrderItem(
                    order,
                    itemDTO.getProductId(),
                    itemDTO.getProductName(),
                    itemDTO.getProductImage(),
                    unitPrice,
                    itemDTO.getQuantity()
            );
        }
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO addOrderItem(String orderId, OrderItemDTO orderItemDTO) {
        Order order = findOrderById(orderId);
        
        Money unitPrice = orderMapper.toMoney(orderItemDTO.getUnitPrice(), orderItemDTO.getCurrencyCode());
        
        orderDomainService.addOrderItem(
                order,
                orderItemDTO.getProductId(),
                orderItemDTO.getProductName(),
                orderItemDTO.getProductImage(),
                unitPrice,
                orderItemDTO.getQuantity()
        );
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO removeOrderItem(String orderId, String orderItemId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.removeOrderItem(order, orderItemId);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO updateOrderItemQuantity(String orderId, String orderItemId, int quantity) {
        Order order = findOrderById(orderId);
        
        orderDomainService.updateOrderItemQuantity(order, orderItemId, quantity);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO payOrder(String orderId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.payOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO shipOrder(String orderId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.shipOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO deliverOrder(String orderId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.deliverOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO completeOrder(String orderId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.completeOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO cancelOrder(String orderId) {
        Order order = findOrderById(orderId);
        
        orderDomainService.cancelOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO updateShippingAddress(String orderId, AddressDTO addressDTO) {
        Order order = findOrderById(orderId);
        
        Address address = orderMapper.toAddress(addressDTO);
        orderDomainService.updateShippingAddress(order, address);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO updateRecipientInfo(String orderId, String recipientName, String recipientPhone) {
        Order order = findOrderById(orderId);
        
        orderDomainService.updateRecipientInfo(order, recipientName, recipientPhone);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId) {
        Order order = findOrderById(orderId);
        return orderMapper.toOrderDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NoSuchElementException("订单不存在: " + orderNumber));
        return orderMapper.toOrderDTO(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toOrderDTOList(orders);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserIdAndStatus(String userId, String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, orderStatus);
        return orderMapper.toOrderDTOList(orders);
    }
    
    /**
     * 根据ID查找订单
     *
     * @param orderId 订单ID
     * @return 订单
     * @throws NoSuchElementException 如果订单不存在
     */
    private Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("订单不存在: " + orderId));
    }
}