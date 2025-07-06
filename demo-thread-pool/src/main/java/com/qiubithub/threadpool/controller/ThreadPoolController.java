package com.qiubithub.threadpool.controller;

import com.qiubithub.threadpool.service.AsyncTaskService;
import com.qiubithub.threadpool.service.BusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 线程池控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/thread-pool")
@RequiredArgsConstructor
public class ThreadPoolController {

    private final AsyncTaskService asyncTaskService;
    private final BusinessService businessService;

    /**
     * 执行IO密集型任务
     *
     * @return 执行结果
     */
    @GetMapping("/io-task")
    public ResponseEntity<Map<String, Object>> executeIoTask() {
        String taskId = UUID.randomUUID().toString();
        CompletableFuture<String> future = asyncTaskService.executeIoTask(taskId);
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("taskId", taskId);
        result.put("message", "IO密集型任务已提交，请稍后查询结果");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 执行CPU密集型任务
     *
     * @return 执行结果
     */
    @GetMapping("/cpu-task")
    public ResponseEntity<Map<String, Object>> executeCpuTask() {
        String taskId = UUID.randomUUID().toString();
        CompletableFuture<String> future = asyncTaskService.executeCpuTask(taskId);
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("taskId", taskId);
        result.put("message", "CPU密集型任务已提交，请稍后查询结果");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 执行混合型任务
     *
     * @return 执行结果
     */
    @GetMapping("/mixed-task")
    public ResponseEntity<Map<String, Object>> executeMixedTask() {
        String taskId = UUID.randomUUID().toString();
        CompletableFuture<String> future = asyncTaskService.executeMixedTask(taskId);
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("taskId", taskId);
        result.put("message", "混合型任务已提交，请稍后查询结果");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 执行多个任务并等待所有任务完成
     *
     * @return 执行结果
     */
    @GetMapping("/all-tasks")
    public ResponseEntity<Map<String, Object>> executeAllTasks() {
        String ioTaskId = UUID.randomUUID().toString();
        String cpuTaskId = UUID.randomUUID().toString();
        String mixedTaskId = UUID.randomUUID().toString();
        
        CompletableFuture<String> ioFuture = asyncTaskService.executeIoTask(ioTaskId);
        CompletableFuture<String> cpuFuture = asyncTaskService.executeCpuTask(cpuTaskId);
        CompletableFuture<String> mixedFuture = asyncTaskService.executeMixedTask(mixedTaskId);
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(ioFuture, cpuFuture, mixedFuture);
        
        try {
            allFutures.get(); // 阻塞等待所有任务完成
            
            Map<String, Object> result = new HashMap<>(4);
            result.put("ioTaskResult", ioFuture.get());
            result.put("cpuTaskResult", cpuFuture.get());
            result.put("mixedTaskResult", mixedFuture.get());
            result.put("message", "所有任务已完成");
            
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("任务执行异常", e);
            
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", "TASK_FAILED");
            result.put("message", "任务执行异常: " + e.getMessage());
            
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 使用分布式锁处理业务
     *
     * @param businessId 业务ID
     * @return 处理结果
     */
    @GetMapping("/business/with-lock/{businessId}")
    public ResponseEntity<Map<String, Object>> processBusinessWithLock(@PathVariable String businessId) {
        String result = businessService.purchaseMembershipWithLock(businessId);
        
        Map<String, Object> response = new HashMap<>(3);
        response.put("code", "SUCCESS");
        response.put("businessId", businessId);
        response.put("result", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 不使用分布式锁处理业务
     *
     * @param businessId 业务ID
     * @return 处理结果
     */
    @GetMapping("/business/without-lock/{businessId}")
    public ResponseEntity<Map<String, Object>> processBusinessWithoutLock(@PathVariable String businessId) {
        String result = businessService.purchaseMembershipWithoutLock(businessId);
        
        Map<String, Object> response = new HashMap<>(3);
        response.put("code", "SUCCESS");
        response.put("businessId", businessId);
        response.put("result", result);
        
        return ResponseEntity.ok(response);
    }
} 