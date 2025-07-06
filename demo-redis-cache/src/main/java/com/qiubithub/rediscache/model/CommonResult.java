package com.qiubithub.rediscache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 通用响应对象
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }
    
    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(500, message, null);
    }
}