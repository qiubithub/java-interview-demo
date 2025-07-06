package com.qiubithub.es.service;

import java.util.Map;

/**
 * 索引管理服务接口
 * 提供索引的创建、删除、更新映射等高级功能
 */
public interface IndexManagementService {

    /**
     * 创建索引
     * @param indexName 索引名称
     * @param modelType 模型类型 (user, product, blog)
     * @return 创建结果
     */
    boolean createIndex(String indexName, String modelType);

    /**
     * 删除索引
     * @param indexName 索引名称
     * @return 删除结果
     */
    boolean deleteIndex(String indexName);

    /**
     * 检查索引是否存在
     * @param indexName 索引名称
     * @return 是否存在
     */
    boolean indexExists(String indexName);

    /**
     * 获取索引映射信息
     * @param indexName 索引名称
     * @return 映射信息
     */
    Map<String, Object> getIndexMapping(String indexName);

    /**
     * 更新索引映射
     * @param indexName 索引名称
     * @param modelType 模型类型 (user, product, blog)
     * @return 更新结果
     */
    boolean updateIndexMapping(String indexName, String modelType);

    /**
     * 获取索引设置
     * @param indexName 索引名称
     * @return 索引设置
     */
    Map<String, Object> getIndexSettings(String indexName);

    /**
     * 更新索引设置
     * @param indexName 索引名称
     * @param settings 索引设置
     * @return 更新结果
     */
    boolean updateIndexSettings(String indexName, Map<String, Object> settings);

    /**
     * 获取所有索引
     * @return 索引列表
     */
    Map<String, Object> getAllIndices();

    /**
     * 获取索引统计信息
     * @param indexName 索引名称
     * @return 统计信息
     */
    Map<String, Object> getIndexStats(String indexName);

    /**
     * 重建索引
     * @param sourceIndex 源索引
     * @param targetIndex 目标索引
     * @param modelType 模型类型 (user, product, blog)
     * @return 重建结果
     */
    boolean reindex(String sourceIndex, String targetIndex, String modelType);
} 