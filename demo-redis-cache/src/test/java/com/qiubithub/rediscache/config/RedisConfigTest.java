package com.qiubithub.rediscache.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Redis配置测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("测试Redis连接")
    void testRedisConnection() {
        // 测试Redis连接
        Boolean result = redisTemplate.hasKey("test");
        assertNotNull(result);
    }

    @Test
    @DisplayName("测试Redis存取值")
    void testRedisOperations() {
        // 存储字符串
        redisTemplate.opsForValue().set("test:string", "Hello Redis");
        
        // 获取字符串
        Object value = redisTemplate.opsForValue().get("test:string");
        assertEquals("Hello Redis", value);
        
        // 删除键
        Boolean deleted = redisTemplate.delete("test:string");
        assertTrue(deleted);
        
        // 验证键已删除
        assertNull(redisTemplate.opsForValue().get("test:string"));
    }

    @Test
    @DisplayName("测试Redis存取对象")
    void testRedisObjectOperations() {
        // 创建测试对象
        TestObject testObject = new TestObject(1L, "测试对象", "这是一个测试对象");
        
        // 存储对象
        redisTemplate.opsForValue().set("test:object", testObject);
        
        // 获取对象
        Object value = redisTemplate.opsForValue().get("test:object");
        assertNotNull(value);
        assertTrue(value instanceof TestObject);
        
        TestObject retrievedObject = (TestObject) value;
        assertEquals(1L, retrievedObject.getId());
        assertEquals("测试对象", retrievedObject.getName());
        assertEquals("这是一个测试对象", retrievedObject.getDescription());
        
        // 删除键
        redisTemplate.delete("test:object");
    }

    @Test
    @DisplayName("测试缓存管理器")
    void testCacheManager() {
        assertNotNull(cacheManager);
        assertNotNull(cacheManager.getCache("user"));
    }

    /**
     * 测试对象
     */
    static class TestObject {
        private Long id;
        private String name;
        private String description;

        public TestObject() {
        }

        public TestObject(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}