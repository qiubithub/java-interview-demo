package com.qiubithub.ddd.infrastructure.exception;

import com.qiubithub.ddd.infrastructure.common.CommonResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public CommonResult<String> handleIllegalStateException(IllegalStateException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        return CommonResult.failed(e.getMessage());
    }
    
    /**
     * 处理参数异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常: {}", e.getMessage(), e);
        return CommonResult.validateFailed(e.getMessage());
    }
    
    /**
     * 处理资源不存在异常
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public CommonResult<String> handleNoSuchElementException(NoSuchElementException e) {
        log.error("资源不存在: {}", e.getMessage(), e);
        return CommonResult.failed(404, e.getMessage());
    }
    
    /**
     * 处理请求参数校验异常 (JSR-303 @Valid)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("请求参数校验异常: {}", message);
        return CommonResult.validateFailed(message);
    }
    
    /**
     * 处理请求参数绑定异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CommonResult<String> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("请求参数绑定异常: {}", message);
        return CommonResult.validateFailed(message);
    }
    
    /**
     * 处理请求参数校验异常 (JSR-303 @Validated)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("请求参数校验异常: {}", message);
        return CommonResult.validateFailed(message);
    }
    
    /**
     * 处理其他异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return CommonResult.failed("系统异常，请联系管理员");
    }
}