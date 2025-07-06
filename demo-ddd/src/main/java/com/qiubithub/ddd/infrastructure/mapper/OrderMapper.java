package com.qiubithub.ddd.infrastructure.mapper;

import com.qiubithub.ddd.domain.model.aggregate.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 插入订单
     *
     * @param order 订单
     * @return 影响的行数
     */
    int insert(Order order);
    
    /**
     * 更新订单
     *
     * @param order 订单
     * @return 影响的行数
     */
    int update(Order order);
    
    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单
     */
    Order selectById(@Param("id") String id);
    
    /**
     * 根据订单编号查询订单
     *
     * @param orderNumber 订单编号
     * @return 订单
     */
    Order selectByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") String userId);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
    
    /**
     * 根据ID删除订单
     *
     * @param id 订单ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);
}