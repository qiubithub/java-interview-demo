package com.qiubithub.ddd.interfaces.rest.controller;

import com.qiubithub.ddd.application.dto.AddressDTO;
import com.qiubithub.ddd.application.dto.CreateOrderDTO;
import com.qiubithub.ddd.application.dto.OrderDTO;
import com.qiubithub.ddd.application.dto.OrderItemDTO;
import com.qiubithub.ddd.application.service.OrderApplicationService;
import com.qiubithub.ddd.infrastructure.common.CommonResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    
    private final OrderApplicationService orderApplicationService;
    
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }
    
    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单请求
     * @return 创建的订单
     */
    @PostMapping
    public CommonResult<OrderDTO> createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        OrderDTO orderDTO = orderApplicationService.createOrder(createOrderDTO);
        return CommonResult.success("订单创建成功", orderDTO);
    }
    
    /**
     * 根据ID获取订单
     *
     * @param orderId 订单ID
     * @return 订单
     */
    @GetMapping("/{orderId}")
    public CommonResult<OrderDTO> getOrderById(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.getOrderById(orderId);
        return CommonResult.success(orderDTO);
    }
    
    /**
     * 根据订单编号获取订单
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    @GetMapping("/number/{orderNumber}")
    public CommonResult<OrderDTO> getOrderByOrderNumber(@PathVariable @NotBlank String orderNumber) {
        OrderDTO orderDTO = orderApplicationService.getOrderByOrderNumber(orderNumber);
        return CommonResult.success(orderDTO);
    }
    
    /**
     * 获取用户的所有订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/user/{userId}")
    public CommonResult<List<OrderDTO>> getOrdersByUserId(@PathVariable @NotBlank String userId) {
        List<OrderDTO> orderDTOList = orderApplicationService.getOrdersByUserId(userId);
        return CommonResult.success(orderDTOList);
    }
    
    /**
     * 获取用户的特定状态订单
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    @GetMapping("/user/{userId}/status/{status}")
    public CommonResult<List<OrderDTO>> getOrdersByUserIdAndStatus(
            @PathVariable @NotBlank String userId,
            @PathVariable @NotBlank String status) {
        List<OrderDTO> orderDTOList = orderApplicationService.getOrdersByUserIdAndStatus(userId, status);
        return CommonResult.success(orderDTOList);
    }
    
    /**
     * 向订单添加商品
     *
     * @param orderId      订单ID
     * @param orderItemDTO 订单项
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/items")
    public CommonResult<OrderDTO> addOrderItem(
            @PathVariable @NotBlank String orderId,
            @RequestBody @Valid OrderItemDTO orderItemDTO) {
        OrderDTO orderDTO = orderApplicationService.addOrderItem(orderId, orderItemDTO);
        return CommonResult.success("商品添加成功", orderDTO);
    }
    
    /**
     * 从订单中移除商品
     *
     * @param orderId     订单ID
     * @param orderItemId 订单项ID
     * @return 更新后的订单
     */
    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public CommonResult<OrderDTO> removeOrderItem(
            @PathVariable @NotBlank String orderId,
            @PathVariable @NotBlank String orderItemId) {
        OrderDTO orderDTO = orderApplicationService.removeOrderItem(orderId, orderItemId);
        return CommonResult.success("商品移除成功", orderDTO);
    }
    
    /**
     * 更新订单中商品的数量
     *
     * @param orderId     订单ID
     * @param orderItemId 订单项ID
     * @param quantity    新数量
     * @return 更新后的订单
     */
    @PutMapping("/{orderId}/items/{orderItemId}/quantity/{quantity}")
    public CommonResult<OrderDTO> updateOrderItemQuantity(
            @PathVariable @NotBlank String orderId,
            @PathVariable @NotBlank String orderItemId,
            @PathVariable @Min(1) int quantity) {
        OrderDTO orderDTO = orderApplicationService.updateOrderItemQuantity(orderId, orderItemId, quantity);
        return CommonResult.success("商品数量更新成功", orderDTO);
    }
    
    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/pay")
    public CommonResult<OrderDTO> payOrder(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.payOrder(orderId);
        return CommonResult.success("订单支付成功", orderDTO);
    }
    
    /**
     * 订单发货
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/ship")
    public CommonResult<OrderDTO> shipOrder(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.shipOrder(orderId);
        return CommonResult.success("订单发货成功", orderDTO);
    }
    
    /**
     * 确认订单送达
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/deliver")
    public CommonResult<OrderDTO> deliverOrder(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.deliverOrder(orderId);
        return CommonResult.success("订单送达成功", orderDTO);
    }
    
    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/complete")
    public CommonResult<OrderDTO> completeOrder(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.completeOrder(orderId);
        return CommonResult.success("订单完成成功", orderDTO);
    }
    
    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 更新后的订单
     */
    @PostMapping("/{orderId}/cancel")
    public CommonResult<OrderDTO> cancelOrder(@PathVariable @NotBlank String orderId) {
        OrderDTO orderDTO = orderApplicationService.cancelOrder(orderId);
        return CommonResult.success("订单取消成功", orderDTO);
    }
    
    /**
     * 更新订单收货地址
     *
     * @param orderId     订单ID
     * @param addressDTO 新的收货地址
     * @return 更新后的订单
     */
    @PutMapping("/{orderId}/address")
    public CommonResult<OrderDTO> updateShippingAddress(
            @PathVariable @NotBlank String orderId,
            @RequestBody @Valid AddressDTO addressDTO) {
        OrderDTO orderDTO = orderApplicationService.updateShippingAddress(orderId, addressDTO);
        return CommonResult.success("收货地址更新成功", orderDTO);
    }
    
    /**
     * 更新订单收件人信息
     *
     * @param orderId        订单ID
     * @param recipientName  收件人姓名
     * @param recipientPhone 收件人电话
     * @return 更新后的订单
     */
    @PutMapping("/{orderId}/recipient")
    public CommonResult<OrderDTO> updateRecipientInfo(
            @PathVariable @NotBlank String orderId,
            @RequestParam @NotBlank String recipientName,
            @RequestParam @NotBlank String recipientPhone) {
        OrderDTO orderDTO = orderApplicationService.updateRecipientInfo(orderId, recipientName, recipientPhone);
        return CommonResult.success("收件人信息更新成功", orderDTO);
    }
}