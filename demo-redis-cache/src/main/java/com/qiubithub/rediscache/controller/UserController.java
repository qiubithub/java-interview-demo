package com.qiubithub.rediscache.controller;

import com.qiubithub.rediscache.exception.GlobalException;
import com.qiubithub.rediscache.model.CommonResult;
import com.qiubithub.rediscache.model.User;
import com.qiubithub.rediscache.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    @GetMapping
    public CommonResult<List<User>> getAllUsers() {
        return CommonResult.success(userService.getAllUsers());
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public CommonResult<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(CommonResult::success)
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建后的用户
     */
    @PostMapping
    public CommonResult<User> createUser(@RequestBody User user) {
        return CommonResult.success(userService.createUser(user));
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    public CommonResult<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(CommonResult::success)
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public CommonResult<String> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        return result ? CommonResult.success("删除成功") : CommonResult.failed("用户不存在");
    }
}