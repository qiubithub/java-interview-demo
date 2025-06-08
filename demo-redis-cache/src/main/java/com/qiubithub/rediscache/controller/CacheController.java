package com.qiubithub.rediscache.controller;

import com.qiubithub.rediscache.model.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 缓存控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;

    /**
     * 获取所有缓存名称
     *
     * @return 缓存名称列表
     */
    @GetMapping
    public CommonResult<Map<String, Object>> getAllCacheNames() {
        Map<String, Object> result = new HashMap<>();
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (Objects.nonNull(cache)) {
                result.put(name, "可用");
            }
        });
        return CommonResult.success(result);
    }

    /**
     * 清空指定缓存
     *
     * @param name 缓存名称
     * @return 操作结果
     */
    @DeleteMapping("/{name}")
    public CommonResult<String> clearCache(@PathVariable String name) {
        Cache cache = cacheManager.getCache(name);
        if (Objects.nonNull(cache)) {
            cache.clear();
            log.info("清空缓存: {}", name);
            return CommonResult.success("缓存已清空");
        }
        return CommonResult.failed("缓存不存在");
    }

    /**
     * 清空所有缓存
     *
     * @return 操作结果
     */
    @DeleteMapping
    public CommonResult<String> clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (Objects.nonNull(cache)) {
                cache.clear();
                log.info("清空缓存: {}", name);
            }
        });
        return CommonResult.success("所有缓存已清空");
    }
}