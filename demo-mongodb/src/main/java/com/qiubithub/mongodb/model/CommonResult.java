package com.qiubithub.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用响应对象
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * @param data 返回的数据
     * @param <T>  数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param data    返回的数据
     * @param message 返回的消息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<>(200, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> failed(Integer errorCode, String message) {
        return new CommonResult<>(errorCode, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(500, message, null);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(400, message, null);
    }

    /**
     * 未授权返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> unauthorized(String message) {
        return new CommonResult<>(401, message, null);
    }

    /**
     * 禁止访问返回结果
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> forbidden(String message) {
        return new CommonResult<>(403, message, null);
    }
}