package com.qiubithub.rediscache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * <p>
 * Redis配置类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Configuration
public class RedisConfig implements CachingConfigurer {

    /**
     * 配置Redis模板
     *
     * @param connectionFactory Redis连接工厂
     * @return Redis模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用Jackson2JsonRedisSerializer替换默认序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = jacksonSerializer();
        
        // key采用String序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // hash的key也采用String序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // value采用Jackson序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value也采用Jackson序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器
     *
     * @param connectionFactory Redis连接工厂
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 使用Jackson2JsonRedisSerializer替换默认序列化
        RedisSerializer<Object> serializer = jacksonSerializer();
        
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 缓存有效期30分钟
                .entryTtl(Duration.ofMinutes(30))
                // 使用StringRedisSerializer序列化key
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 使用Jackson2JsonRedisSerializer序列化value
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                // 不缓存null值
                .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * 获取Jackson序列化器
     *
     * @return Jackson序列化器
     */
    private Jackson2JsonRedisSerializer<Object> jacksonSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        
        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}