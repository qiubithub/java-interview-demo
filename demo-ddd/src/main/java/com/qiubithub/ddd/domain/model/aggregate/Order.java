package com.qiubithub.ddd.domain.model.aggregate;

import com.qiubithub.ddd.domain.model.entity.OrderItem;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 订单聚合根
 */
@Getter
@Setter
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        CREATED("已创建"),
        PAID("已支付"),
        SHIPPED("已发货"),
        DELIVERED("已送达"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private String id;
    private String userId;
    private String orderNumber;
    private OrderStatus status;
    private Money totalAmount;
    private Address shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private LocalDateTime createdTime;
    private LocalDateTime paymentTime;
    private LocalDateTime shippingTime;
    private LocalDateTime completionTime;
    private LocalDateTime cancellationTime;
    private Set<OrderItem> orderItems = new HashSet<>();

    protected Order() {
        // JPA需要无参构造函数
    }

    /**
     * 创建订单
     *
     * @param userId          用户ID
     * @param shippingAddress 配送地址
     * @param recipientName   收件人姓名
     * @param recipientPhone  收件人电话
     */
    public Order(String userId, Address shippingAddress, String recipientName, String recipientPhone) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("配送地址不能为空");
        }
        if (StringUtils.isBlank(recipientName)) {
            throw new IllegalArgumentException("收件人姓名不能为空");
        }
        if (StringUtils.isBlank(recipientPhone)) {
            throw new IllegalArgumentException("收件人电话不能为空");
        }

        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.orderNumber = generateOrderNumber();
        this.status = OrderStatus.CREATED;
        this.shippingAddress = shippingAddress;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.createdTime = LocalDateTime.now();
        this.totalAmount = Money.yuan(0);
    }

    /**
     * 添加订单项
     *
     * @param productId    商品ID
     * @param productName  商品名称
     * @param productImage 商品图片
     * @param unitPrice    单价
     * @param quantity     数量
     * @return 添加的订单项
     */
    public OrderItem addOrderItem(String productId, String productName, String productImage, Money unitPrice, int quantity) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付状态的订单才能添加商品");
        }

        // 检查是否已存在相同商品
        Optional<OrderItem> existingItem = orderItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        OrderItem orderItem;
        if (existingItem.isPresent()) {
            // 如果已存在相同商品，更新数量
            orderItem = existingItem.get();
            orderItem.updateQuantity(orderItem.getQuantity() + quantity);
        } else {
            // 创建新的订单项
            orderItem = new OrderItem(this.id, productId, productName, productImage, unitPrice, quantity);
            orderItems.add(orderItem);
        }

        // 重新计算订单总金额
        recalculateTotalAmount();
        return orderItem;
    }

    /**
     * 更新订单项数量
     *
     * @param orderItemId 订单项ID
     * @param quantity    新数量
     */
    public void updateOrderItemQuantity(String orderItemId, int quantity) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付状态的订单才能修改商品数量");
        }

        OrderItem orderItem = findOrderItemById(orderItemId);
        orderItem.updateQuantity(quantity);
        recalculateTotalAmount();
    }

    /**
     * 移除订单项
     *
     * @param orderItemId 订单项ID
     */
    public void removeOrderItem(String orderItemId) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付状态的订单才能移除商品");
        }

        OrderItem orderItem = findOrderItemById(orderItemId);
        orderItems.remove(orderItem);
        recalculateTotalAmount();
    }

    /**
     * 支付订单
     */
    public void pay() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("只有待支付状态的订单才能支付");
        }

        if (orderItems.isEmpty()) {
            throw new IllegalStateException("订单中没有商品，无法支付");
        }

        status = OrderStatus.PAID;
        paymentTime = LocalDateTime.now();
    }

    /**
     * 发货
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付状态的订单才能发货");
        }

        status = OrderStatus.SHIPPED;
        shippingTime = LocalDateTime.now();
    }

    /**
     * 确认送达
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货状态的订单才能确认送达");
        }

        status = OrderStatus.DELIVERED;
    }

    /**
     * 完成订单
     */
    public void complete() {
        if (status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("只有已送达状态的订单才能完成");
        }

        status = OrderStatus.COMPLETED;
        completionTime = LocalDateTime.now();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw new IllegalStateException("只有待支付或已支付状态的订单才能取消");
        }

        status = OrderStatus.CANCELLED;
        cancellationTime = LocalDateTime.now();
    }

    /**
     * 更新收货地址
     *
     * @param shippingAddress 新的收货地址
     */
    public void updateShippingAddress(Address shippingAddress) {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw new IllegalStateException("只有待支付或已支付状态的订单才能修改收货地址");
        }

        if (shippingAddress == null) {
            throw new IllegalArgumentException("配送地址不能为空");
        }

        this.shippingAddress = shippingAddress;
    }

    /**
     * 更新收件人信息
     *
     * @param recipientName  收件人姓名
     * @param recipientPhone 收件人电话
     */
    public void updateRecipientInfo(String recipientName, String recipientPhone) {
        if (status != OrderStatus.CREATED && status != OrderStatus.PAID) {
            throw new IllegalStateException("只有待支付或已支付状态的订单才能修改收件人信息");
        }

        if (StringUtils.isBlank(recipientName)) {
            throw new IllegalArgumentException("收件人姓名不能为空");
        }
        if (StringUtils.isBlank(recipientPhone)) {
            throw new IllegalArgumentException("收件人电话不能为空");
        }

        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }

    /**
     * 查找订单项
     *
     * @param orderItemId 订单项ID
     * @return 订单项
     */
    private OrderItem findOrderItemById(String orderItemId) {
        return orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("订单项不存在: " + orderItemId));
    }

    /**
     * 重新计算订单总金额
     */
    private void recalculateTotalAmount() {
        Money total = Money.yuan(0);
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotal());
        }
        this.totalAmount = total;
    }

    /**
     * 生成订单编号
     *
     * @return 订单编号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}