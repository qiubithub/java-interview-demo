package com.qiubithub.ddd.domain.model.entity;

import com.qiubithub.ddd.domain.model.valueobject.Money;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * 订单项实体
 */
@Getter
@Setter
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private String productImage;
    private Money unitPrice;
    private int quantity;
    private Money subtotal;

    protected OrderItem() {
        // JPA需要无参构造函数
    }

    public OrderItem(String orderId, String productId, String productName, String productImage, Money unitPrice, int quantity) {
        if (StringUtils.isBlank(orderId)) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        if (StringUtils.isBlank(productId)) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (StringUtils.isBlank(productName)) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("商品单价不能为空");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }

        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    /**
     * 更新数量
     *
     * @param newQuantity 新数量
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        this.quantity = newQuantity;
        this.subtotal = calculateSubtotal();
    }

    /**
     * 计算小计金额
     *
     * @return 小计金额
     */
    private Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}