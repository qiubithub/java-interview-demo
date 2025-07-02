package com.qiubithub.threadpool.controller;

import com.qiubithub.threadpool.service.AdvancedAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 高级异步控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/advanced-async")
@RequiredArgsConstructor
public class AdvancedAsyncController {

    private final AdvancedAsyncService advancedAsyncService;

    /**
     * 并行处理列表
     *
     * @param items 数据项，逗号分隔
     * @return 处理结果
     */
    @GetMapping("/parallel")
    public ResponseEntity<Map<String, Object>> processParallel(@RequestParam(defaultValue = "item1,item2,item3") String items) {
        List<String> itemList = Arrays.asList(items.split(","));
        log.info("接收到并行处理请求，数据项: {}", itemList);

        try {
            CompletableFuture<List<String>> future = advancedAsyncService.processListParallel(itemList);
            List<String> results = future.get(); // 等待处理完成

            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "并行处理完成");
            response.put("results", results);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("并行处理异常", e);
            Thread.currentThread().interrupt();

            Map<String, Object> response = new HashMap<>();
            response.put("code", "ERROR");
            response.put("message", "处理异常: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 链式处理
     *
     * @param input 输入字符串
     * @return 处理结果
     */
    @GetMapping("/chain")
    public ResponseEntity<Map<String, Object>> processChain(@RequestParam(defaultValue = "test-input") String input) {
        log.info("接收到链式处理请求，输入: {}", input);

        try {
            CompletableFuture<String> future = advancedAsyncService.processChainedTasks(input);
            String result = future.get(); // 等待处理完成

            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "链式处理完成");
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("链式处理异常", e);
            Thread.currentThread().interrupt();

            Map<String, Object> response = new HashMap<>();
            response.put("code", "ERROR");
            response.put("message", "处理异常: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 组合任务
     *
     * @param param1 参数1
     * @param param2 参数2
     * @return 处理结果
     */
    @GetMapping("/combine")
    public ResponseEntity<Map<String, Object>> processCombine(
            @RequestParam(defaultValue = "param1") String param1,
            @RequestParam(defaultValue = "param2") String param2) {
        log.info("接收到任务组合请求，参数: {} 和 {}", param1, param2);

        try {
            CompletableFuture<String> future = advancedAsyncService.combineAsyncTasks(param1, param2);
            String result = future.get(); // 等待处理完成

            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "组合任务处理完成");
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("组合任务处理异常", e);
            Thread.currentThread().interrupt();

            Map<String, Object> response = new HashMap<>();
            response.put("code", "ERROR");
            response.put("message", "处理异常: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 异常处理
     *
     * @param input 输入字符串
     * @return 处理结果
     */
    @GetMapping("/error-handling")
    public ResponseEntity<Map<String, Object>> processWithErrorHandling(@RequestParam String input) {
        log.info("接收到异常处理请求，输入: {}", input);

        try {
            CompletableFuture<String> future = advancedAsyncService.executeWithErrorHandling(input);
            String result = future.get(); // 等待处理完成

            Map<String, Object> response = new HashMap<>();
            response.put("code", "SUCCESS");
            response.put("message", "异常处理任务完成");
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            log.error("异常处理任务异常", e);
            Thread.currentThread().interrupt();

            Map<String, Object> response = new HashMap<>();
            response.put("code", "ERROR");
            response.put("message", "处理异常: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}