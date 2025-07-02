package com.qiubithub.threadpool.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 通用返回结果
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 通用结果
     */
    public static <T> CommonResult<T> success(T data) {
        return CommonResult.<T>builder()
                .code("SUCCESS")
                .message("操作成功")
                .data(data)
                .build();
    }

    /**
     * 成功结果
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return 通用结果
     */
    public static <T> CommonResult<T> success(String message, T data) {
        return CommonResult.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 失败结果
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(String message) {
        return CommonResult.<T>builder()
                .code("ERROR")
                .message(message)
                .build();
    }

    /**
     * 失败结果
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return 通用结果
     */
    public static <T> CommonResult<T> error(String code, String message) {
        return CommonResult.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}