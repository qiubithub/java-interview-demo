package com.qiubithub.ddd.application.dto;

import com.qiubithub.ddd.domain.model.aggregate.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    /**
     * 订单ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /**
     * 订单编号
     */
    private String orderNumber;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 货币代码
     */
    private String currencyCode;
    
    /**
     * 配送地址
     */
    @NotNull(message = "配送地址不能为空")
    @Valid
    private AddressDTO shippingAddress;
    
    /**
     * 收件人姓名
     */
    @NotBlank(message = "收件人姓名不能为空")
    private String recipientName;
    
    /**
     * 收件人电话
     */
    @NotBlank(message = "收件人电话不能为空")
    private String recipientPhone;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippingTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completionTime;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancellationTime;
    
    /**
     * 订单项列表
     */
    private List<OrderItemDTO> orderItems = new ArrayList<>();
}