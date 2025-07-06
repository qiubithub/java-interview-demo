package com.qiubithub.rediscache.exception;

import com.qiubithub.rediscache.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 * 全局异常处理器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义异常
     *
     * @param e 自定义异常
     * @return 通用返回结果
     */
    @ExceptionHandler(GlobalException.class)
    public CommonResult<String> handleGlobalException(GlobalException e) {
        log.error("全局异常: {}", e.getMessage());
        return new CommonResult<>(Integer.parseInt(e.getCode()), e.getMessage(), null);
    }
    
    /**
     * 处理未知异常
     *
     * @param e 未知异常
     * @return 通用返回结果
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception e) {
        log.error("未知异常: ", e);
        return CommonResult.failed("系统异常，请联系管理员");
    }
}