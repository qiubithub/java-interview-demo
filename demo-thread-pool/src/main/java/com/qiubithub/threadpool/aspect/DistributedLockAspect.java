package com.qiubithub.threadpool.aspect;

import com.qiubithub.threadpool.annotation.DistributedLockable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.*;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 分布式锁切面
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 环绕通知，拦截带有分布式锁注解的方法
     *
     * @param joinPoint 连接点
     * @param lockable  分布式锁注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(lockable)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLockable lockable) throws Throwable {
        String lockKey = getLockKey(joinPoint, lockable);

        if (!StringUtils.hasText(lockKey)) {
            log.warn("分布式锁的键值为空，不执行加锁操作");
            return joinPoint.proceed();
        }

        // 根据锁类型获取对应的锁对象
        RLock lock = getLockByType(lockKey, lockable);
        
        boolean acquired = false;
        try {
            // 尝试获取锁
            acquired = tryAcquireLock(lock, lockable);
            
            if (acquired) {
                log.debug("获取分布式锁成功，key: {}, lockType: {}", lockKey, lockable.lockType());
                return joinPoint.proceed();
            } else {
                log.warn("获取分布式锁失败，key: {}, lockType: {}", lockKey, lockable.lockType());
                return handleLockFailure(joinPoint, lockable);
            }
        } finally {
            if (acquired && lock != null) {
                releaseLock(lock, lockKey);
            }
        }
    }

    /**
     * 根据锁类型获取对应的锁对象
     */
    private RLock getLockByType(String lockKey, DistributedLockable lockable) {
        String key = lockable.prefix() + ":" + lockKey;
        
        switch (lockable.lockType()) {
            case REENTRANT_LOCK:
                return redissonClient.getLock(key);
            case FAIR_LOCK:
                return redissonClient.getFairLock(key);
            case READ_LOCK:
                return redissonClient.getReadWriteLock(key).readLock();
            case WRITE_LOCK:
                return redissonClient.getReadWriteLock(key).writeLock();
            default:
                return redissonClient.getLock(key);
        }
    }

    /**
     * 尝试获取锁
     */
    private boolean tryAcquireLock(RLock lock, DistributedLockable lockable) throws InterruptedException {
        long waitTime = lockable.waitTime();
        long leaseTime = lockable.leaseTime();
        TimeUnit timeUnit = lockable.timeUnit();

        if (leaseTime > 0) {
            // 指定租约时间
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } else {
            // 使用看门狗机制（自动续期）
            return lock.tryLock(waitTime, timeUnit);
        }
    }

    /**
     * 释放锁
     */
    private void releaseLock(RLock lock, String lockKey) {
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁成功，key: {}", lockKey);
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，key: {}", lockKey, e);
        }
    }

    /**
     * 处理获取锁失败的情况
     */
    private Object handleLockFailure(ProceedingJoinPoint joinPoint, DistributedLockable lockable) throws Throwable {
        if (lockable.failFast()) {
            // 快速失败，抛出异常
            Map<String, Object> response = new HashMap<>();
            response.put("code", "LOCK_FAILED");
            response.put("message", "获取分布式锁失败，请稍后重试");
            return response;
        } else {
            // 返回fallback值
            String fallbackValue = lockable.fallbackValue();
            if (StringUtils.hasText(fallbackValue)) {
                return evaluateExpression(fallbackValue, joinPoint);
            }
            
            // 返回方法返回类型的默认值
            Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
            if (returnType == void.class || returnType == Void.class) {
                return null;
            } else if (returnType.isPrimitive()) {
                return getPrimitiveDefaultValue(returnType);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取原始类型的默认值
     */
    private Object getPrimitiveDefaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == char.class) return '\u0000';
        return null;
    }

    /**
     * 获取分布式锁的键值
     *
     * @param joinPoint 连接点
     * @param lockable  分布式锁注解
     * @return 锁键值
     */
    private String getLockKey(ProceedingJoinPoint joinPoint, DistributedLockable lockable) {
        String key = lockable.key();
        if (!StringUtils.hasText(key)) {
            return null;
        }

        // 如果包含SpEL表达式，进行解析
        if (key.contains("#") || key.contains("$")) {
            return evaluateExpression(key, joinPoint);
        }

        return key;
    }

    /**
     * 解析SpEL表达式
     *
     * @param expression 表达式
     * @param joinPoint  连接点
     * @return 解析结果
     */
    private String evaluateExpression(String expression, ProceedingJoinPoint joinPoint) {
        try {
            Expression expr = expressionParser.parseExpression(expression);
            EvaluationContext context = createEvaluationContext(joinPoint);
            Object value = expr.getValue(context);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            log.error("解析SpEL表达式失败: {}", expression, e);
            return expression;
        }
    }

    /**
     * 创建SpEL表达式上下文
     *
     * @param joinPoint 连接点
     * @return 评估上下文
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 获取方法参数名和值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (parameterNames != null && args != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // 设置方法相关变量
        context.setVariable("methodName", method.getName());
        context.setVariable("className", joinPoint.getTarget().getClass().getSimpleName());

        return context;
    }
} 