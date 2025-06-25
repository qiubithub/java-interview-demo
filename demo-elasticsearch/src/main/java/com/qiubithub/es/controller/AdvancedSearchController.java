package com.qiubithub.es.controller;

import com.qiubithub.es.model.Blog;
import com.qiubithub.es.model.Product;
import com.qiubithub.es.model.User;
import com.qiubithub.es.service.AdvancedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 高级搜索控制器
 * 提供复杂查询、聚合分析等高级功能
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.es.enabled", havingValue = "true")
public class AdvancedSearchController {

    private final AdvancedSearchService searchService;

    /**
     * 多字段组合搜索产品
     * @param keyword 关键词
     * @param category 分类
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param brand 品牌
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = searchService.searchProducts(keyword, category, minPrice, maxPrice, brand, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多字段组合搜索博客
     * @param keyword 关键词
     * @param author 作者
     * @param category 分类
     * @param tags 标签
     * @param published 是否发布
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/blogs")
    public ResponseEntity<Map<String, Object>> searchBlogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = searchService.searchBlogs(keyword, author, category, tags, published, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 多字段组合搜索用户
     * @param keyword 关键词
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @param gender 性别
     * @param tags 标签
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = searchService.searchUsers(keyword, minAge, maxAge, gender, tags, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 产品聚合分析
     * @return 聚合结果
     */
    @GetMapping("/products/aggregations")
    public ResponseEntity<Map<String, Object>> productAggregations() {
        Map<String, Object> result = searchService.productAggregations();
        return ResponseEntity.ok(result);
    }

    /**
     * 博客聚合分析
     * @return 聚合结果
     */
    @GetMapping("/blogs/aggregations")
    public ResponseEntity<Map<String, Object>> blogAggregations() {
        Map<String, Object> result = searchService.blogAggregations();
        return ResponseEntity.ok(result);
    }

    /**
     * 用户聚合分析
     * @return 聚合结果
     */
    @GetMapping("/users/aggregations")
    public ResponseEntity<Map<String, Object>> userAggregations() {
        Map<String, Object> result = searchService.userAggregations();
        return ResponseEntity.ok(result);
    }

    /**
     * 全文搜索（搜索所有类型）
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/fulltext")
    public ResponseEntity<Map<String, Object>> fullTextSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = searchService.fullTextSearch(keyword, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 复杂搜索（高级DSL查询）
     * @param query 查询DSL
     * @return 搜索结果
     */
    @PostMapping("/complex")
    public ResponseEntity<Map<String, Object>> complexSearch(@RequestBody String query) {
        Map<String, Object> result = searchService.complexSearch(query);
        return ResponseEntity.ok(result);
    }
} 