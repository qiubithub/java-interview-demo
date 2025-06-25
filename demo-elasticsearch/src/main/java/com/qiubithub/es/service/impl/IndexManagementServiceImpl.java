package com.qiubithub.es.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.es.model.Blog;
import com.qiubithub.es.model.Product;
import com.qiubithub.es.model.User;
import com.qiubithub.es.service.IndexManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 索引管理服务实现类
 * 提供索引的创建、删除、更新映射等高级功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.es.enabled", havingValue = "true")
public class IndexManagementServiceImpl implements IndexManagementService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Value("${app.es.indices.product:products}")
    private String productIndex;

    @Value("${app.es.indices.blog:blogs}")
    private String blogIndex;

    @Value("${app.es.indices.user:users}")
    private String userIndex;

    /**
     * 创建索引
     */
    @Override
    public boolean createIndex(String indexName, String modelType) {
        try {
            // 检查索引是否存在
            if (indexExists(indexName)) {
                log.info("索引 {} 已存在", indexName);
                return false;
            }

            // 创建索引请求
            CreateIndexRequest request = new CreateIndexRequest(indexName);

            // 设置分片和副本
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 1)
                    .put("index.max_result_window", 10000)
                    .build());

            // 根据模型类型设置映射
            String mappingJson = getMappingJson(modelType);
            if (mappingJson != null) {
                request.mapping(mappingJson, XContentType.JSON);
            }

            // 设置别名
            request.alias(new Alias(modelType + "_alias"));

            // 创建索引
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("创建索引 {} 失败: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除索引
     */
    @Override
    public boolean deleteIndex(String indexName) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                log.info("索引 {} 不存在", indexName);
                return false;
            }

            // 创建删除索引请求
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);

            // 删除索引
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("删除索引 {} 失败: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查索引是否存在
     */
    @Override
    public boolean indexExists(String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("检查索引 {} 是否存在失败: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取索引映射信息
     */
    @Override
    public Map<String, Object> getIndexMapping(String indexName) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                return Map.of(
                        "success", false,
                        "message", "索引不存在",
                        "indexName", indexName
                );
            }

            // 创建获取映射请求
            GetMappingsRequest request = new GetMappingsRequest().indices(indexName);

            // 获取映射
            GetMappingsResponse response = restHighLevelClient.indices().getMapping(request, RequestOptions.DEFAULT);
            
            // 处理响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("indexName", indexName);
            result.put("mappings", response.mappings().get(indexName).getSourceAsMap());
            
            return result;
        } catch (IOException e) {
            log.error("获取索引 {} 映射失败: {}", indexName, e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "获取索引映射失败: " + e.getMessage(),
                    "indexName", indexName
            );
        }
    }

    /**
     * 更新索引映射
     */
    @Override
    public boolean updateIndexMapping(String indexName, String modelType) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                log.info("索引 {} 不存在", indexName);
                return false;
            }

            // 获取映射JSON
            String mappingJson = getMappingJson(modelType);
            if (mappingJson == null) {
                log.info("未找到模型类型 {} 的映射", modelType);
                return false;
            }

            // 创建更新映射请求
            PutMappingRequest request = new PutMappingRequest(indexName);
            request.source(mappingJson, XContentType.JSON);

            // 更新映射
            AcknowledgedResponse response = restHighLevelClient.indices().putMapping(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("更新索引 {} 映射失败: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取索引设置
     */
    @Override
    public Map<String, Object> getIndexSettings(String indexName) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                return Map.of(
                        "success", false,
                        "message", "索引不存在",
                        "indexName", indexName
                );
            }

            // 创建获取设置请求
            GetSettingsRequest request = new GetSettingsRequest().indices(indexName);

            // 获取设置
            GetSettingsResponse response = restHighLevelClient.indices().getSettings(request, RequestOptions.DEFAULT);
            
            // 处理响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("indexName", indexName);
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("number_of_shards", response.getSetting(indexName, "index.number_of_shards"));
            settings.put("number_of_replicas", response.getSetting(indexName, "index.number_of_replicas"));
            settings.put("creation_date", response.getSetting(indexName, "index.creation_date"));
            settings.put("uuid", response.getSetting(indexName, "index.uuid"));
            settings.put("provided_name", response.getSetting(indexName, "index.provided_name"));
            
            result.put("settings", settings);
            
            return result;
        } catch (IOException e) {
            log.error("获取索引 {} 设置失败: {}", indexName, e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "获取索引设置失败: " + e.getMessage(),
                    "indexName", indexName
            );
        }
    }

    /**
     * 更新索引设置
     */
    @Override
    public boolean updateIndexSettings(String indexName, Map<String, Object> settings) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                log.info("索引 {} 不存在", indexName);
                return false;
            }

            // 创建更新设置请求
            UpdateSettingsRequest request = new UpdateSettingsRequest(indexName);
            
            // 构建设置
            Settings.Builder settingsBuilder = Settings.builder();
            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                settingsBuilder.put("index." + entry.getKey(), entry.getValue().toString());
            }
            
            request.settings(settingsBuilder);

            // 更新设置
            AcknowledgedResponse response = restHighLevelClient.indices().putSettings(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("更新索引 {} 设置失败: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取所有索引
     */
    @Override
    public Map<String, Object> getAllIndices() {
        try {
            // 创建获取索引请求
            GetIndexRequest request = new GetIndexRequest("*");

            // 获取索引
            GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
            
            // 处理响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            
            Map<String, Object> indices = new HashMap<>();
            for (String indexName : response.getIndices()) {
                Map<String, Object> indexInfo = new HashMap<>();
                indexInfo.put("mappings", response.getMappings().get(indexName).getSourceAsMap());
                
                Map<String, Object> settings = new HashMap<>();
                settings.put("number_of_shards", response.getSetting(indexName, "index.number_of_shards"));
                settings.put("number_of_replicas", response.getSetting(indexName, "index.number_of_replicas"));
                settings.put("creation_date", response.getSetting(indexName, "index.creation_date"));
                
                indexInfo.put("settings", settings);
                indices.put(indexName, indexInfo);
            }
            
            result.put("indices", indices);
            
            return result;
        } catch (IOException e) {
            log.error("获取所有索引失败: {}", e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "获取所有索引失败: " + e.getMessage()
            );
        }
    }

    /**
     * 获取索引统计信息
     */
    @Override
    public Map<String, Object> getIndexStats(String indexName) {
        try {
            // 检查索引是否存在
            if (!indexExists(indexName)) {
                return Map.of(
                        "success", false,
                        "message", "索引不存在",
                        "indexName", indexName
                );
            }

            // 创建搜索请求获取文档数量
            org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(indexName);
            searchRequest.source().size(0);
            
            org.elasticsearch.action.search.SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            
            // 处理响应
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("indexName", indexName);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("doc_count", searchResponse.getHits().getTotalHits().value);
            stats.put("took", searchResponse.getTook().getMillis());
            
            // 获取索引设置
            Map<String, Object> settings = getIndexSettings(indexName);
            if ((boolean) settings.get("success")) {
                stats.put("settings", settings.get("settings"));
            }
            
            result.put("stats", stats);
            
            return result;
        } catch (IOException e) {
            log.error("获取索引 {} 统计信息失败: {}", indexName, e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "message", "获取索引统计信息失败: " + e.getMessage(),
                    "indexName", indexName
            );
        }
    }

    /**
     * 重建索引
     */
    @Override
    public boolean reindex(String sourceIndex, String targetIndex, String modelType) {
        try {
            // 检查源索引是否存在
            if (!indexExists(sourceIndex)) {
                log.info("源索引 {} 不存在", sourceIndex);
                return false;
            }
            
            // 检查目标索引是否存在，如果不存在则创建
            if (!indexExists(targetIndex)) {
                boolean created = createIndex(targetIndex, modelType);
                if (!created) {
                    log.info("创建目标索引 {} 失败", targetIndex);
                    return false;
                }
            }
            
            // 创建重建索引请求
            ReindexRequest request = new ReindexRequest();
            request.setSourceIndices(sourceIndex);
            request.setDestIndex(targetIndex);
            
            // 设置查询条件（可选）
            request.setSourceQuery(QueryBuilders.matchAllQuery());
            
            // 设置批量大小
            request.setSourceBatchSize(1000);
            
            // 执行重建索引
            org.elasticsearch.index.reindex.BulkByScrollResponse response = 
                    restHighLevelClient.reindex(request, RequestOptions.DEFAULT);
            
            // 检查结果
            return response.getBulkFailures().isEmpty() && response.getSearchFailures().isEmpty();
        } catch (IOException e) {
            log.error("重建索引从 {} 到 {} 失败: {}", sourceIndex, targetIndex, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据模型类型获取映射JSON
     */
    private String getMappingJson(String modelType) {
        try {
            Map<String, Object> mapping = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();
            
            if ("user".equalsIgnoreCase(modelType)) {
                // 用户映射
                properties.put("id", Map.of("type", "keyword"));
                properties.put("username", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("nickname", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("email", Map.of("type", "keyword"));
                properties.put("age", Map.of("type", "integer"));
                properties.put("gender", Map.of("type", "keyword"));
                properties.put("address", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart"
                ));
                properties.put("tags", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("createTime", Map.of("type", "date"));
                properties.put("updateTime", Map.of("type", "date"));
            } else if ("product".equalsIgnoreCase(modelType)) {
                // 产品映射
                properties.put("id", Map.of("type", "keyword"));
                properties.put("name", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("description", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart"
                ));
                properties.put("price", Map.of("type", "double"));
                properties.put("category", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("brand", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("tags", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("createTime", Map.of("type", "date"));
                properties.put("updateTime", Map.of("type", "date"));
            } else if ("blog".equalsIgnoreCase(modelType)) {
                // 博客映射
                properties.put("id", Map.of("type", "keyword"));
                properties.put("title", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("content", Map.of(
                        "type", "text",
                        "analyzer", "ik_max_word",
                        "search_analyzer", "ik_smart"
                ));
                properties.put("author", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("category", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("tags", Map.of(
                        "type", "text",
                        "fields", Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256))
                ));
                properties.put("published", Map.of("type", "boolean"));
                properties.put("createTime", Map.of("type", "date"));
                properties.put("updateTime", Map.of("type", "date"));
            } else {
                return null;
            }
            
            mapping.put("properties", properties);
            return objectMapper.writeValueAsString(mapping);
        } catch (Exception e) {
            log.error("生成映射JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }
} 