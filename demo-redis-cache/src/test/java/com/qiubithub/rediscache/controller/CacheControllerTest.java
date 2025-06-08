package com.qiubithub.rediscache.controller;

import com.qiubithub.rediscache.model.CommonResult;
import com.qiubithub.rediscache.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 缓存控制器测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("测试获取所有缓存名称")
    void testGetAllCacheNames() {
        // 先访问用户接口，确保缓存被创建
        userService.getUserById(1L);
        
        String url = "http://localhost:" + port + "/redis-cache/api/cache";
        
        ResponseEntity<CommonResult<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResult<Map<String, Object>>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData().containsKey("user"));
    }

    @Test
    @DisplayName("测试清空指定缓存")
    void testClearCache() {
        // 先访问用户接口，确保缓存被创建
        userService.getUserById(1L);
        
        String url = "http://localhost:" + port + "/redis-cache/api/cache/user";
        
        ResponseEntity<CommonResult<String>> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<CommonResult<String>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("缓存已清空", response.getBody().getData());
        
        // 再次访问用户接口，应该从数据库获取
        long startTime = System.currentTimeMillis();
        userService.getUserById(1L);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        // 第二次访问，应该从缓存获取
        startTime = System.currentTimeMillis();
        userService.getUserById(1L);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        // 第二次调用应该比第一次快
        System.out.println("清除缓存后第一次调用耗时: " + firstCallTime + "ms");
        System.out.println("清除缓存后第二次调用耗时: " + secondCallTime + "ms");
    }

    @Test
    @DisplayName("测试清空所有缓存")
    void testClearAllCaches() {
        // 先访问用户接口，确保缓存被创建
        userService.getUserById(1L);
        
        String url = "http://localhost:" + port + "/redis-cache/api/cache";
        
        ResponseEntity<CommonResult<String>> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<CommonResult<String>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("所有缓存已清空", response.getBody().getData());
    }
}