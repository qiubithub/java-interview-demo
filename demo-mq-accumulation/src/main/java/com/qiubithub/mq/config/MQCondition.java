package com.qiubithub.mq.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 条件注解，用于控制RocketMQ相关组件是否启用
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "app.mq.enabled", havingValue = "true", matchIfMissing = true)
public @interface MQCondition {
}