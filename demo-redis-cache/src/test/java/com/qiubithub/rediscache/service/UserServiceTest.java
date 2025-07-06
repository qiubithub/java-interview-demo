package com.qiubithub.rediscache.service;

import com.qiubithub.rediscache.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 用户服务测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("测试获取用户")
    void testGetUserById() {
        // 第一次调用，应该从数据库获取
        long startTime = System.currentTimeMillis();
        Optional<User> user1 = userService.getUserById(1L);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        assertTrue(user1.isPresent());
        assertEquals("admin", user1.get().getUsername());
        
        // 第二次调用，应该从缓存获取，速度更快
        startTime = System.currentTimeMillis();
        Optional<User> user2 = userService.getUserById(1L);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertTrue(user2.isPresent());
        assertEquals("admin", user2.get().getUsername());
        
        // 第二次调用应该比第一次快
        System.out.println("第一次调用耗时: " + firstCallTime + "ms");
        System.out.println("第二次调用耗时: " + secondCallTime + "ms");
        
        // 验证缓存是否存在
        assertNotNull(cacheManager.getCache("user"));
    }

    @Test
    @DisplayName("测试获取所有用户")
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 3);
    }

    @Test
    @DisplayName("测试创建用户")
    void testCreateUser() {
        User newUser = User.builder()
                .username("testUser")
                .password("password123")
                .email("test@example.com")
                .phone("13800138004")
                .build();
        
        User createdUser = userService.createUser(newUser);
        
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("testUser", createdUser.getUsername());
        assertNotNull(createdUser.getCreateTime());
        assertNotNull(createdUser.getUpdateTime());
        
        // 验证用户是否已创建
        Optional<User> fetchedUser = userService.getUserById(createdUser.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals("testUser", fetchedUser.get().getUsername());
    }

    @Test
    @DisplayName("测试更新用户")
    void testUpdateUser() {
        // 先创建一个用户
        User newUser = User.builder()
                .username("updateTest")
                .password("password123")
                .email("update@example.com")
                .phone("13800138005")
                .build();
        
        User createdUser = userService.createUser(newUser);
        
        // 更新用户
        User updateData = new User();
        updateData.setUsername("updatedName");
        updateData.setEmail("updated@example.com");
        
        Optional<User> updatedUser = userService.updateUser(createdUser.getId(), updateData);
        
        assertTrue(updatedUser.isPresent());
        assertEquals("updatedName", updatedUser.get().getUsername());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
        assertEquals("13800138005", updatedUser.get().getPhone());
    }

    @Test
    @DisplayName("测试删除用户")
    void testDeleteUser() {
        // 先创建一个用户
        User newUser = User.builder()
                .username("deleteTest")
                .password("password123")
                .email("delete@example.com")
                .phone("13800138006")
                .build();
        
        User createdUser = userService.createUser(newUser);
        
        // 验证用户已创建
        Optional<User> fetchedUser = userService.getUserById(createdUser.getId());
        assertTrue(fetchedUser.isPresent());
        
        // 删除用户
        boolean deleted = userService.deleteUser(createdUser.getId());
        assertTrue(deleted);
        
        // 验证用户已删除
        Optional<User> deletedUser = userService.getUserById(createdUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}