package com.qiubithub.threadpool.controller;

import com.qiubithub.threadpool.model.ThreadPoolStats;
import com.qiubithub.threadpool.service.ThreadPoolMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 线程池监控控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/thread-pool/monitor")
@RequiredArgsConstructor
public class ThreadPoolMonitorController {

    private final ThreadPoolMonitorService threadPoolMonitorService;

    /**
     * 获取所有线程池的统计信息
     *
     * @return 所有线程池的统计信息
     */
    @GetMapping
    public ResponseEntity<List<ThreadPoolStats>> getAllThreadPoolStats() {
        List<ThreadPoolStats> statsList = threadPoolMonitorService.getAllThreadPoolStats();
        return ResponseEntity.ok(statsList);
    }

    /**
     * 获取指定线程池的统计信息
     *
     * @param poolName 线程池名称
     * @return 线程池统计信息
     */
    @GetMapping("/{poolName}")
    public ResponseEntity<ThreadPoolStats> getThreadPoolStats(@PathVariable String poolName) {
        ThreadPoolStats stats = threadPoolMonitorService.getThreadPoolStats(poolName);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * 动态调整线程池参数
     *
     * @param poolName      线程池名称
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param queueCapacity 队列容量
     * @return 调整后的线程池统计信息
     */
    @PutMapping("/{poolName}")
    public ResponseEntity<Map<String, Object>> updateThreadPoolConfig(
            @PathVariable String poolName,
            @RequestParam(required = false) Integer corePoolSize,
            @RequestParam(required = false) Integer maxPoolSize,
            @RequestParam(required = false) Integer queueCapacity) {

        ThreadPoolStats stats = threadPoolMonitorService.updateThreadPoolConfig(poolName, corePoolSize, maxPoolSize, queueCapacity);

        Map<String, Object> response = new HashMap<>(3);
        if (stats == null) {
            response.put("code", "NOT_FOUND");
            response.put("message", "线程池不存在：" + poolName);
            return ResponseEntity.ok(response);
        }

        response.put("code", "SUCCESS");
        response.put("message", "线程池参数调整成功");
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }
}