package com.qiubithub.ddd.application.mapper;

import com.qiubithub.ddd.application.dto.AddressDTO;
import com.qiubithub.ddd.application.dto.OrderDTO;
import com.qiubithub.ddd.application.dto.OrderItemDTO;
import com.qiubithub.ddd.domain.model.aggregate.Order;
import com.qiubithub.ddd.domain.model.entity.OrderItem;
import com.qiubithub.ddd.domain.model.valueobject.Address;
import com.qiubithub.ddd.domain.model.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 订单对象映射器
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    /**
     * 将订单聚合根转换为DTO
     *
     * @param order 订单聚合根
     * @return 订单DTO
     */
    @Mapping(source = "totalAmount.amount", target = "totalAmount")
    @Mapping(source = "totalAmount.currencyCode", target = "currencyCode")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapOrderStatus")
    OrderDTO toOrderDTO(Order order);
    
    /**
     * 将订单聚合根集合转换为DTO集合
     *
     * @param orders 订单聚合根集合
     * @return 订单DTO集合
     */
    List<OrderDTO> toOrderDTOList(List<Order> orders);
    
    /**
     * 将订单项实体转换为DTO
     *
     * @param orderItem 订单项实体
     * @return 订单项DTO
     */
    @Mapping(source = "unitPrice.amount", target = "unitPrice")
    @Mapping(source = "unitPrice.currencyCode", target = "currencyCode")
    @Mapping(source = "subtotal.amount", target = "subtotal")
    OrderItemDTO toOrderItemDTO(OrderItem orderItem);
    
    /**
     * 将订单项实体集合转换为DTO集合
     *
     * @param orderItems 订单项实体集合
     * @return 订单项DTO集合
     */
    List<OrderItemDTO> toOrderItemDTOList(Set<OrderItem> orderItems);
    
    /**
     * 将地址值对象转换为DTO
     *
     * @param address 地址值对象
     * @return 地址DTO
     */
    AddressDTO toAddressDTO(Address address);
    
    /**
     * 将地址DTO转换为值对象
     *
     * @param addressDTO 地址DTO
     * @return 地址值对象
     */
    Address toAddress(AddressDTO addressDTO);
    
    /**
     * 将金额和货币代码转换为金额值对象
     *
     * @param amount       金额
     * @param currencyCode 货币代码
     * @return 金额值对象
     */
    default Money toMoney(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode);
    }
    
    /**
     * 将订单状态枚举转换为字符串
     *
     * @param status 订单状态枚举
     * @return 状态字符串
     */
    @Named("mapOrderStatus")
    default String mapOrderStatus(Order.OrderStatus status) {
        return status.name();
    }
}