package com.qiubithub.ddd.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    /**
     * 订单项ID
     */
    private String id;
    
    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String productId;
    
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
    
    /**
     * 货币代码
     */
    @NotBlank(message = "货币代码不能为空")
    private String currencyCode;
    
    /**
     * 数量
     */
    @Min(value = 1, message = "数量必须大于0")
    private int quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
}