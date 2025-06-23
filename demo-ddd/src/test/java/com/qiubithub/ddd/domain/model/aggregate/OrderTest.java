package com.qiubithub.ddd.domain.model.aggregate;

import com.qiubithub.ddd.domain.model.entity.OrderItem;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单聚合根单元测试
 */
class OrderTest {

    @Test
    @DisplayName("测试创建订单")
    void testCreateOrder() {
        // Given
        String userId = UUID.randomUUID().toString();
        Address address = new Address("广东省", "深圳市", "南山区", "科技园路", "10号", "518000");
        String recipientName = "张三";
        String recipientPhone = "13800138000";

        // When
        Order order = new Order(userId, address, recipientName, recipientPhone);

        // Then
        assertNotNull(order.getId());
        assertEquals(userId, order.getUserId());
        assertEquals(address, order.getShippingAddress());
        assertEquals(recipientName, order.getRecipientName());
        assertEquals(recipientPhone, order.getRecipientPhone());
        assertEquals(Order.OrderStatus.CREATED, order.getStatus());
        assertTrue(order.getOrderItems().isEmpty());
        assertEquals(Money.yuan(0).getAmount(), order.getTotalAmount().getAmount());
    }

    @Test
    @DisplayName("测试添加订单项")
    void testAddOrderItem() {
        // Given
        Order order = createSampleOrder();
        String productId = "product-001";
        String productName = "测试商品";
        String productImage = "test.jpg";
        Money unitPrice = Money.yuan(100);
        int quantity = 2;

        // When
        OrderItem orderItem = order.addOrderItem(productId, productName, productImage, unitPrice, quantity);

        // Then
        assertNotNull(orderItem);
        assertEquals(1, order.getOrderItems().size());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(productImage, orderItem.getProductImage());
        assertEquals(unitPrice, orderItem.getUnitPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(Money.yuan(200).getAmount(), orderItem.getSubtotal().getAmount());
        assertEquals(Money.yuan(200).getAmount(), order.getTotalAmount().getAmount());
    }

    @Test
    @DisplayName("测试更新订单项数量")
    void testUpdateOrderItemQuantity() {
        // Given
        Order order = createSampleOrder();
        OrderItem orderItem = order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        int newQuantity = 3;

        // When
        order.updateOrderItemQuantity(orderItem.getId(), newQuantity);

        // Then
        assertEquals(newQuantity, orderItem.getQuantity());
        assertEquals(Money.yuan(300).getAmount(), orderItem.getSubtotal().getAmount());
        assertEquals(Money.yuan(300).getAmount(), order.getTotalAmount().getAmount());
    }

    @Test
    @DisplayName("测试移除订单项")
    void testRemoveOrderItem() {
        // Given
        Order order = createSampleOrder();
        OrderItem orderItem = order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);

        // When
        order.removeOrderItem(orderItem.getId());

        // Then
        assertTrue(order.getOrderItems().isEmpty());
        assertEquals(Money.yuan(0).getAmount(), order.getTotalAmount().getAmount());
    }

    @Test
    @DisplayName("测试支付订单")
    void testPayOrder() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);

        // When
        order.pay();

        // Then
        assertEquals(Order.OrderStatus.PAID, order.getStatus());
        assertNotNull(order.getPaymentTime());
    }

    @Test
    @DisplayName("测试订单发货")
    void testShipOrder() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay();

        // When
        order.ship();

        // Then
        assertEquals(Order.OrderStatus.SHIPPED, order.getStatus());
        assertNotNull(order.getShippingTime());
    }

    @Test
    @DisplayName("测试确认订单送达")
    void testDeliverOrder() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay();
        order.ship();

        // When
        order.deliver();

        // Then
        assertEquals(Order.OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    @DisplayName("测试完成订单")
    void testCompleteOrder() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay();
        order.ship();
        order.deliver();

        // When
        order.complete();

        // Then
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
        assertNotNull(order.getCompletionTime());
    }

    @Test
    @DisplayName("测试取消订单")
    void testCancelOrder() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);

        // When
        order.cancel();

        // Then
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getCancellationTime());
    }

    @Test
    @DisplayName("测试更新收货地址")
    void testUpdateShippingAddress() {
        // Given
        Order order = createSampleOrder();
        Address newAddress = new Address("北京市", "海淀区", "中关村", "科学院南路", "2号", "100080");

        // When
        order.updateShippingAddress(newAddress);

        // Then
        assertEquals(newAddress, order.getShippingAddress());
    }

    @Test
    @DisplayName("测试更新收件人信息")
    void testUpdateRecipientInfo() {
        // Given
        Order order = createSampleOrder();
        String newName = "李四";
        String newPhone = "13900139000";

        // When
        order.updateRecipientInfo(newName, newPhone);

        // Then
        assertEquals(newName, order.getRecipientName());
        assertEquals(newPhone, order.getRecipientPhone());
    }

    @Test
    @DisplayName("测试非创建状态不能添加商品")
    void testAddOrderItemWhenStatusNotCreated() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay(); // 改变状态为已支付

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            order.addOrderItem("product-002", "测试商品2", "test2.jpg", Money.yuan(200), 1);
        });
    }

    @Test
    @DisplayName("测试非创建状态不能移除商品")
    void testRemoveOrderItemWhenStatusNotCreated() {
        // Given
        Order order = createSampleOrder();
        OrderItem orderItem = order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay(); // 改变状态为已支付

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            order.removeOrderItem(orderItem.getId());
        });
    }

    @Test
    @DisplayName("测试非创建状态不能更新商品数量")
    void testUpdateOrderItemQuantityWhenStatusNotCreated() {
        // Given
        Order order = createSampleOrder();
        OrderItem orderItem = order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay(); // 改变状态为已支付

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            order.updateOrderItemQuantity(orderItem.getId(), 3);
        });
    }

    @Test
    @DisplayName("测试只有待支付状态才能支付")
    void testPayOrderWhenStatusNotCreated() {
        // Given
        Order order = createSampleOrder();
        order.addOrderItem("product-001", "测试商品", "test.jpg", Money.yuan(100), 2);
        order.pay(); // 改变状态为已支付

        // When & Then
        assertThrows(IllegalStateException.class, order::pay);
    }

    /**
     * 创建样例订单
     */
    private Order createSampleOrder() {
        String userId = UUID.randomUUID().toString();
        Address address = new Address("广东省", "深圳市", "南山区", "科技园路", "10号", "518000");
        String recipientName = "张三";
        String recipientPhone = "13800138000";
        return new Order(userId, address, recipientName, recipientPhone);
    }
}