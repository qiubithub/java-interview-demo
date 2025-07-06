package com.qiubithub.es.controller;

import com.qiubithub.es.model.Blog;
import com.qiubithub.es.model.Product;
import com.qiubithub.es.model.User;
import com.qiubithub.es.service.IndexManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 索引管理控制器
 * 提供索引的创建、删除、更新映射等高级功能
 */
@RestController
@RequestMapping("/api/indices")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.es.enabled", havingValue = "true")
public class IndexManagementController {

    private final IndexManagementService indexManagementService;

    /**
     * 创建索引
     * @param indexName 索引名称
     * @param modelType 模型类型 (user, product, blog)
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createIndex(
            @RequestParam String indexName,
            @RequestParam String modelType) {
        boolean result = indexManagementService.createIndex(indexName, modelType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "索引创建成功" : "索引创建失败");
        response.put("indexName", indexName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除索引
     * @param indexName 索引名称
     * @return 删除结果
     */
    @DeleteMapping("/{indexName}")
    public ResponseEntity<Map<String, Object>> deleteIndex(@PathVariable String indexName) {
        boolean result = indexManagementService.deleteIndex(indexName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "索引删除成功" : "索引删除失败");
        response.put("indexName", indexName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 检查索引是否存在
     * @param indexName 索引名称
     * @return 检查结果
     */
    @GetMapping("/{indexName}/exists")
    public ResponseEntity<Map<String, Object>> indexExists(@PathVariable String indexName) {
        boolean exists = indexManagementService.indexExists(indexName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("indexName", indexName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取索引映射信息
     * @param indexName 索引名称
     * @return 映射信息
     */
    @GetMapping("/{indexName}/mapping")
    public ResponseEntity<Map<String, Object>> getIndexMapping(@PathVariable String indexName) {
        Map<String, Object> mapping = indexManagementService.getIndexMapping(indexName);
        return ResponseEntity.ok(mapping);
    }

    /**
     * 更新索引映射
     * @param indexName 索引名称
     * @param modelType 模型类型 (user, product, blog)
     * @return 更新结果
     */
    @PutMapping("/{indexName}/mapping")
    public ResponseEntity<Map<String, Object>> updateIndexMapping(
            @PathVariable String indexName,
            @RequestParam String modelType) {
        boolean result = indexManagementService.updateIndexMapping(indexName, modelType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "索引映射更新成功" : "索引映射更新失败");
        response.put("indexName", indexName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取索引设置
     * @param indexName 索引名称
     * @return 索引设置
     */
    @GetMapping("/{indexName}/settings")
    public ResponseEntity<Map<String, Object>> getIndexSettings(@PathVariable String indexName) {
        Map<String, Object> settings = indexManagementService.getIndexSettings(indexName);
        return ResponseEntity.ok(settings);
    }

    /**
     * 更新索引设置
     * @param indexName 索引名称
     * @param settings 索引设置
     * @return 更新结果
     */
    @PutMapping("/{indexName}/settings")
    public ResponseEntity<Map<String, Object>> updateIndexSettings(
            @PathVariable String indexName,
            @RequestBody Map<String, Object> settings) {
        boolean result = indexManagementService.updateIndexSettings(indexName, settings);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "索引设置更新成功" : "索引设置更新失败");
        response.put("indexName", indexName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有索引
     * @return 索引列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllIndices() {
        Map<String, Object> indices = indexManagementService.getAllIndices();
        return ResponseEntity.ok(indices);
    }

    /**
     * 获取索引统计信息
     * @param indexName 索引名称
     * @return 统计信息
     */
    @GetMapping("/{indexName}/stats")
    public ResponseEntity<Map<String, Object>> getIndexStats(@PathVariable String indexName) {
        Map<String, Object> stats = indexManagementService.getIndexStats(indexName);
        return ResponseEntity.ok(stats);
    }

    /**
     * 重建索引
     * @param sourceIndex 源索引
     * @param targetIndex 目标索引
     * @param modelType 模型类型 (user, product, blog)
     * @return 重建结果
     */
    @PostMapping("/reindex")
    public ResponseEntity<Map<String, Object>> reindex(
            @RequestParam String sourceIndex,
            @RequestParam String targetIndex,
            @RequestParam String modelType) {
        boolean result = indexManagementService.reindex(sourceIndex, targetIndex, modelType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "重建索引成功" : "重建索引失败");
        response.put("sourceIndex", sourceIndex);
        response.put("targetIndex", targetIndex);
        
        return ResponseEntity.ok(response);
    }
} 