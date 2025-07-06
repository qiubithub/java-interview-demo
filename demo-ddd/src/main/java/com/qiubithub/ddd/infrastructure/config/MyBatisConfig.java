package com.qiubithub.ddd.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.qiubithub.ddd.infrastructure.mapper")
public class MyBatisConfig {
    // 可以在这里添加额外的MyBatis配置
}