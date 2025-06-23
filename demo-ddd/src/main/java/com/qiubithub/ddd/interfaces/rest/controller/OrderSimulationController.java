package com.qiubithub.ddd.interfaces.rest.controller;

import com.qiubithub.ddd.application.dto.AddressDTO;
import com.qiubithub.ddd.application.dto.CreateOrderDTO;
import com.qiubithub.ddd.application.dto.OrderDTO;
import com.qiubithub.ddd.application.dto.OrderItemDTO;
import com.qiubithub.ddd.application.service.OrderApplicationService;
import com.qiubithub.ddd.infrastructure.common.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单模拟控制器 - 用于展示DDD架构的基本操作流程
 */
@RestController
@RequestMapping("/api/simulation/orders")
public class OrderSimulationController {
    
    private final OrderApplicationService orderApplicationService;
    
    public OrderSimulationController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }
    
    /**
     * 模拟完整的订单流程：创建->支付->发货->送达->完成
     *
     * @return 操作结果
     */
    @GetMapping("/simulate-order-flow")
    public CommonResult<List<OrderDTO>> simulateOrderFlow() {
        List<OrderDTO> results = new ArrayList<>();
        
        // 1. 创建订单
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 创建地址
        AddressDTO addressDTO = new AddressDTO(
                "广东省", "深圳市", "南山区", "科技园路", "10号楼5层", "518000"
        );
        
        // 创建订单项
        List<OrderItemDTO> orderItems = new ArrayList<>();
        
        // 添加商品1
        OrderItemDTO item1 = new OrderItemDTO();
        item1.setProductId("product_001");
        item1.setProductName("高端笔记本电脑");
        item1.setProductImage("laptop.jpg");
        item1.setUnitPrice(new BigDecimal("9999.00"));
        item1.setCurrencyCode("CNY");
        item1.setQuantity(1);
        orderItems.add(item1);
        
        // 添加商品2
        OrderItemDTO item2 = new OrderItemDTO();
        item2.setProductId("product_002");
        item2.setProductName("无线鼠标");
        item2.setProductImage("mouse.jpg");
        item2.setUnitPrice(new BigDecimal("199.00"));
        item2.setCurrencyCode("CNY");
        item2.setQuantity(2);
        orderItems.add(item2);
        
        // 创建订单请求
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(
                userId, addressDTO, "张三", "13800138000", orderItems
        );
        
        // 创建订单
        OrderDTO createdOrder = orderApplicationService.createOrder(createOrderDTO);
        results.add(createdOrder);
        
        // 2. 支付订单
        OrderDTO paidOrder = orderApplicationService.payOrder(createdOrder.getId());
        results.add(paidOrder);
        
        // 3. 发货
        OrderDTO shippedOrder = orderApplicationService.shipOrder(paidOrder.getId());
        results.add(shippedOrder);
        
        // 4. 确认送达
        OrderDTO deliveredOrder = orderApplicationService.deliverOrder(shippedOrder.getId());
        results.add(deliveredOrder);
        
        // 5. 完成订单
        OrderDTO completedOrder = orderApplicationService.completeOrder(deliveredOrder.getId());
        results.add(completedOrder);
        
        return CommonResult.success("订单流程模拟完成", results);
    }
    
    /**
     * 模拟订单取消流程
     *
     * @return 操作结果
     */
    @GetMapping("/simulate-order-cancel")
    public CommonResult<List<OrderDTO>> simulateOrderCancel() {
        List<OrderDTO> results = new ArrayList<>();
        
        // 1. 创建订单
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 创建地址
        AddressDTO addressDTO = new AddressDTO(
                "北京市", "海淀区", "中关村", "科学院南路", "2号楼3单元", "100080"
        );
        
        // 创建订单项
        List<OrderItemDTO> orderItems = new ArrayList<>();
        
        // 添加商品
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId("product_003");
        item.setProductName("智能手机");
        item.setProductImage("phone.jpg");
        item.setUnitPrice(new BigDecimal("4999.00"));
        item.setCurrencyCode("CNY");
        item.setQuantity(1);
        orderItems.add(item);
        
        // 创建订单请求
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(
                userId, addressDTO, "李四", "13900139000", orderItems
        );
        
        // 创建订单
        OrderDTO createdOrder = orderApplicationService.createOrder(createOrderDTO);
        results.add(createdOrder);
        
        // 2. 取消订单
        OrderDTO cancelledOrder = orderApplicationService.cancelOrder(createdOrder.getId());
        results.add(cancelledOrder);
        
        return CommonResult.success("订单取消流程模拟完成", results);
    }
    
    /**
     * 模拟订单修改流程
     *
     * @return 操作结果
     */
    @GetMapping("/simulate-order-update")
    public CommonResult<List<OrderDTO>> simulateOrderUpdate() {
        List<OrderDTO> results = new ArrayList<>();
        
        // 1. 创建订单
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 创建地址
        AddressDTO addressDTO = new AddressDTO(
                "上海市", "浦东新区", "张江", "科技园路", "88号", "201203"
        );
        
        // 创建订单项
        List<OrderItemDTO> orderItems = new ArrayList<>();
        
        // 添加商品
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId("product_004");
        item.setProductName("平板电脑");
        item.setProductImage("tablet.jpg");
        item.setUnitPrice(new BigDecimal("3999.00"));
        item.setCurrencyCode("CNY");
        item.setQuantity(1);
        orderItems.add(item);
        
        // 创建订单请求
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(
                userId, addressDTO, "王五", "13600136000", orderItems
        );
        
        // 创建订单
        OrderDTO createdOrder = orderApplicationService.createOrder(createOrderDTO);
        results.add(createdOrder);
        
        // 2. 添加新商品
        OrderItemDTO newItem = new OrderItemDTO();
        newItem.setProductId("product_005");
        newItem.setProductName("蓝牙耳机");
        newItem.setProductImage("headphone.jpg");
        newItem.setUnitPrice(new BigDecimal("699.00"));
        newItem.setCurrencyCode("CNY");
        newItem.setQuantity(1);
        
        OrderDTO updatedOrder1 = orderApplicationService.addOrderItem(createdOrder.getId(), newItem);
        results.add(updatedOrder1);
        
        // 3. 更新收货地址
        AddressDTO newAddress = new AddressDTO(
                "上海市", "浦东新区", "陆家嘴", "世纪大道", "100号", "200120"
        );
        
        OrderDTO updatedOrder2 = orderApplicationService.updateShippingAddress(updatedOrder1.getId(), newAddress);
        results.add(updatedOrder2);
        
        // 4. 更新收件人信息
        OrderDTO updatedOrder3 = orderApplicationService.updateRecipientInfo(
                updatedOrder2.getId(), "赵六", "13500135000"
        );
        results.add(updatedOrder3);
        
        return CommonResult.success("订单修改流程模拟完成", results);
    }
}