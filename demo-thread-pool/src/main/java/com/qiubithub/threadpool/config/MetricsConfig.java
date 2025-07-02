package com.qiubithub.threadpool.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 指标监控配置类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Configuration
public class MetricsConfig {

    /**
     * 创建TimedAspect，用于支持@Timed注解
     *
     * @param registry 指标注册表
     * @return TimedAspect实例
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}