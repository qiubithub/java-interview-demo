package com.qiubithub.ddd.infrastructure.mapper;

import com.qiubithub.ddd.domain.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项Mapper接口
 */
@Mapper
public interface OrderItemMapper {
    
    /**
     * 批量插入订单项
     *
     * @param orderItems 订单项列表
     * @return 影响的行数
     */
    int batchInsert(@Param("list") List<OrderItem> orderItems);
    
    /**
     * 更新订单项
     *
     * @param orderItem 订单项
     * @return 影响的行数
     */
    int update(OrderItem orderItem);
    
    /**
     * 根据ID查询订单项
     *
     * @param id 订单项ID
     * @return 订单项
     */
    OrderItem selectById(@Param("id") String id);
    
    /**
     * 根据订单ID查询订单项列表
     *
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);
    
    /**
     * 根据ID删除订单项
     *
     * @param id 订单项ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 根据订单ID删除订单项
     *
     * @param orderId 订单ID
     * @return 影响的行数
     */
    int deleteByOrderId(@Param("orderId") String orderId);
}