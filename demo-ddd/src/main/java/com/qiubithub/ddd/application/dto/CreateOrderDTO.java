package com.qiubithub.ddd.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建订单数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    
    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
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
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemDTO> orderItems = new ArrayList<>();
}