package com.qiubithub.ddd.infrastructure.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用返回结果封装类
 *
 * @param <T> 数据类型
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
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     * @param data    返回数据
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(200, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(500, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> failed(Integer code, String message) {
        return new CommonResult<>(code, message, null);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(400, message, null);
    }

    /**
     * 未授权返回结果
     *
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<>(401, "暂未登录或token已经过期", null);
    }

    /**
     * 未授权返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> unauthorized(String message) {
        return new CommonResult<>(401, message, null);
    }

    /**
     * 禁止访问返回结果
     *
     * @param <T> 数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<>(403, "没有相关权限", null);
    }

    /**
     * 禁止访问返回结果
     *
     * @param message 提示信息
     * @param <T>     数据类型
     * @return 通用返回结果
     */
    public static <T> CommonResult<T> forbidden(String message) {
        return new CommonResult<>(403, message, null);
    }
}