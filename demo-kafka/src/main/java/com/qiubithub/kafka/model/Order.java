package com.qiubithub.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private String status;
    private String productId;
    private Integer quantity;
}