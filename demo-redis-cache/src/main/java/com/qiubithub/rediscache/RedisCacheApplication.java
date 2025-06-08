package com.qiubithub.rediscache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * <p>
 * Redis缓存示例应用
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootApplication
@EnableCaching
public class RedisCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisCacheApplication.class, args);
    }
}