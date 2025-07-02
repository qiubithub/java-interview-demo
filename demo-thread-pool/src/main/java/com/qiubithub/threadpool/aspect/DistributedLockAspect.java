package com.qiubithub.threadpool.aspect;

import com.qiubithub.threadpool.annotation.DistributedLockable;
import com.qiubithub.threadpool.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLock distributedLock;
    
    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    /**
     * 参数名发现器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 环绕通知，拦截带有分布式锁注解的方法
     *
     * @param joinPoint 连接点
     * @param lockable  分布式锁注解
     * @return 方法执行结果
     * @throws Throwable 异常信息
     */
    @Around("@annotation(lockable)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLockable lockable) throws Throwable {
        String lockKey = getLockKey(joinPoint, lockable);
        
        if (StringUtils.isEmpty(lockKey)) {
            log.warn("分布式锁的键值为空，不执行加锁操作");
            return joinPoint.proceed();
        }
        
        boolean locked = false;
        try {
            // 尝试获取锁
            locked = distributedLock.tryLock(lockKey, lockable.leaseTime(), lockable.waitTime(), lockable.timeUnit());
            
            if (locked) {
                log.debug("获取分布式锁成功，key: {}", lockKey);
                return joinPoint.proceed();
            } else {
                log.warn("获取分布式锁失败，key: {}", lockKey);
                // 返回友好的响应，而不是抛出异常
                Map<String, Object> response = new HashMap<>(2);
                response.put("code", "LOCK_FAILED");
                response.put("message", "系统繁忙，请稍后重试");
                return ResponseEntity.ok(response);
            }
        } finally {
            if (locked) {
                distributedLock.unlock(lockKey);
                log.debug("释放分布式锁，key: {}", lockKey);
            }
        }
    }

    /**
     * 获取分布式锁的键值
     *
     * @param joinPoint 连接点
     * @param lockable  分布式锁注解
     * @return 锁的键值
     */
    private String getLockKey(ProceedingJoinPoint joinPoint, DistributedLockable lockable) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法参数名和参数值
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Object[] args = joinPoint.getArgs();
        
        if (Objects.isNull(parameterNames) || parameterNames.length == 0) {
            return lockable.prefix() + ":" + lockable.key();
        }
        
        // 创建SpEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        
        // 解析SpEL表达式
        Expression expression = expressionParser.parseExpression(lockable.key());
        String key = expression.getValue(context, String.class);
        
        return lockable.prefix() + ":" + key;
    }
} 