package com.qiubithub.ddd.application.service;

import com.qiubithub.ddd.application.dto.AddressDTO;
import com.qiubithub.ddd.application.dto.CreateOrderDTO;
import com.qiubithub.ddd.application.dto.OrderDTO;
import com.qiubithub.ddd.application.dto.OrderItemDTO;

import java.util.List;

/**
 * 订单应用服务接口
 */
public interface OrderApplicationService {
    
    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单请求
     * @return 创建的订单
     */
    OrderDTO createOrder(CreateOrderDTO createOrderDTO);
    
    /**
     * 向订单添加商品
     *
     * @param orderId      订单ID
     * @param orderItemDTO 订单项
     * @return 更新后的订单
     */
    OrderDTO addOrderItem(String orderId, OrderItemDTO orderItemDTO);
    
    /**
     * 从订单中移除商品
     *
     * @param orderId     订单ID
     * @param orderItemId 订单项ID
     * @return 更新后的订单
     */
    OrderDTO removeOrderItem(String orderId, String orderItemId);
    
    /**
     * 更新订单中商品的数量
     *
     * @param orderId     订单ID
     * @param orderItemId 订单项ID
     * @param quantity    新数量
     * @return 更新后的订单
     */
    OrderDTO updateOrderItemQuantity(String orderId, String orderItemId, int quantity);
    
    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    OrderDTO payOrder(String orderId);
    
    /**
     * 订单发货
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    OrderDTO shipOrder(String orderId);
    
    /**
     * 确认订单送达
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    OrderDTO deliverOrder(String orderId);
    
    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    OrderDTO completeOrder(String orderId);
    
    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    OrderDTO cancelOrder(String orderId);
    
    /**
     * 更新订单收货地址
     *
     * @param orderId     订单ID
     * @param addressDTO 新的收货地址
     * @return 更新后的订单
     */
    OrderDTO updateShippingAddress(String orderId, AddressDTO addressDTO);
    
    /**
     * 更新订单收件人信息
     *
     * @param orderId        订单ID
     * @param recipientName  收件人姓名
     * @param recipientPhone 收件人电话
     * @return 更新后的订单
     */
    OrderDTO updateRecipientInfo(String orderId, String recipientName, String recipientPhone);
    
    /**
     * 根据ID获取订单
     *
     * @param orderId 订单ID
     * @return 订单
     */
    OrderDTO getOrderById(String orderId);
    
    /**
     * 根据订单编号获取订单
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    OrderDTO getOrderByOrderNumber(String orderNumber);
    
    /**
     * 获取用户的所有订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderDTO> getOrdersByUserId(String userId);
    
    /**
     * 获取用户的特定状态订单
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderDTO> getOrdersByUserIdAndStatus(String userId, String status);
}