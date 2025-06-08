package com.qiubithub.rediscache.service.impl;

import com.qiubithub.rediscache.model.User;
import com.qiubithub.rediscache.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 用户服务实现类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /**
     * 模拟数据库
     */
    private static final Map<Long, User> USER_DB = new ConcurrentHashMap<>();

    static {
        // 初始化一些用户数据
        LocalDateTime now = LocalDateTime.now();
        USER_DB.put(1L, User.builder().id(1L).username("admin").password("admin123").email("admin@example.com").phone("13800138001").createTime(now).updateTime(now).build());
        USER_DB.put(2L, User.builder().id(2L).username("user").password("user123").email("user@example.com").phone("13800138002").createTime(now).updateTime(now).build());
        USER_DB.put(3L, User.builder().id(3L).username("test").password("test123").email("test@example.com").phone("13800138003").createTime(now).updateTime(now).build());
    }

    @Override
    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public Optional<User> getUserById(Long id) {
        log.info("从数据库中获取用户信息，用户ID: {}", id);
        
        // 模拟数据库查询延迟
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程被中断", e);
        }
        
        return Optional.ofNullable(USER_DB.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        log.info("从数据库中获取所有用户信息");
        return new ArrayList<>(USER_DB.values());
    }

    @Override
    public User createUser(User user) {
        log.info("创建用户: {}", user);
        
        // 参数校验
        if (user == null || StringUtils.isBlank(user.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        
        // 生成ID
        Long id = USER_DB.size() + 1L;
        user.setId(id);
        
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 保存用户
        USER_DB.put(id, user);
        
        return user;
    }

    @Override
    @CachePut(value = "user", key = "#id", unless = "#result == null")
    public Optional<User> updateUser(Long id, User user) {
        log.info("更新用户，用户ID: {}, 用户信息: {}", id, user);
        
        // 参数校验
        if (id == null || user == null) {
            return Optional.empty();
        }
        
        // 检查用户是否存在
        if (!USER_DB.containsKey(id)) {
            return Optional.empty();
        }
        
        // 获取原用户信息
        User existUser = USER_DB.get(id);
        
        // 更新用户信息
        if (StringUtils.isNotBlank(user.getUsername())) {
            existUser.setUsername(user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            existUser.setPassword(user.getPassword());
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            existUser.setEmail(user.getEmail());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            existUser.setPhone(user.getPhone());
        }
        
        // 更新时间
        existUser.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        USER_DB.put(id, existUser);
        
        return Optional.of(existUser);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public boolean deleteUser(Long id) {
        log.info("删除用户，用户ID: {}", id);
        
        // 参数校验
        if (id == null) {
            return false;
        }
        
        // 检查用户是否存在
        if (!USER_DB.containsKey(id)) {
            return false;
        }
        
        // 删除用户
        USER_DB.remove(id);
        
        return true;
    }
}