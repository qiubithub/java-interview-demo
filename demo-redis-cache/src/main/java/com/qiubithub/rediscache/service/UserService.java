package com.qiubithub.rediscache.service;

import com.qiubithub.rediscache.model.User;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 用户服务接口
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public interface UserService {

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> getUserById(Long id);

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建后的用户
     */
    User createUser(User user);

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户
     */
    Optional<User> updateUser(Long id, User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);
}