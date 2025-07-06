package com.qiubithub.rediscache.exception;

import lombok.Getter;

/**
 * <p>
 * 全局异常类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Getter
public class GlobalException extends RuntimeException {
    
    private final String code;
    private final String message;
    
    public GlobalException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public GlobalException(String message) {
        this("500", message);
    }
    
    /**
     * 用户不存在异常
     */
    public static GlobalException USER_NOT_FOUND = new GlobalException("404", "用户不存在");
    
    /**
     * 参数错误异常
     */
    public static GlobalException PARAM_ERROR = new GlobalException("400", "参数错误");
    
    /**
     * 系统错误异常
     */
    public static GlobalException SYSTEM_ERROR = new GlobalException("500", "系统错误");
}