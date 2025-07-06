package com.qiubithub.es.service;

import java.util.List;
import java.util.Map;

/**
 * 高级搜索服务接口
 * 提供复杂查询、聚合分析等高级功能
 */
public interface AdvancedSearchService {

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
    Map<String, Object> searchProducts(String keyword, String category, Double minPrice, Double maxPrice, String brand, int page, int size);

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
    Map<String, Object> searchBlogs(String keyword, String author, String category, List<String> tags, Boolean published, int page, int size);

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
    Map<String, Object> searchUsers(String keyword, Integer minAge, Integer maxAge, String gender, List<String> tags, int page, int size);

    /**
     * 产品聚合分析
     * @return 聚合结果
     */
    Map<String, Object> productAggregations();

    /**
     * 博客聚合分析
     * @return 聚合结果
     */
    Map<String, Object> blogAggregations();

    /**
     * 用户聚合分析
     * @return 聚合结果
     */
    Map<String, Object> userAggregations();

    /**
     * 全文搜索（搜索所有类型）
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    Map<String, Object> fullTextSearch(String keyword, int page, int size);

    /**
     * 复杂搜索（高级DSL查询）
     * @param query 查询DSL
     * @return 搜索结果
     */
    Map<String, Object> complexSearch(String query);
} 