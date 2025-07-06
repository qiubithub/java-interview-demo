package com.qiubithub.threadpool.controller;

import com.qiubithub.threadpool.service.ScheduledTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 定时任务控制器
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@Slf4j
@RestController
@RequestMapping("/api/scheduled-tasks")
@RequiredArgsConstructor
public class ScheduledTaskController {

    private final ScheduledTaskService scheduledTaskService;

    /**
     * 启动定时任务
     *
     * @return 操作结果
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startScheduledTasks() {
        scheduledTaskService.startScheduledTasks();

        Map<String, Object> response = new HashMap<>(2);
        response.put("code", "SUCCESS");
        response.put("message", "定时任务已启动");

        return ResponseEntity.ok(response);
    }

    /**
     * 停止定时任务
     *
     * @return 操作结果
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopScheduledTasks() {
        scheduledTaskService.stopScheduledTasks();

        Map<String, Object> response = new HashMap<>(2);
        response.put("code", "SUCCESS");
        response.put("message", "定时任务已停止");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取定时任务状态
     *
     * @return 定时任务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getScheduledTasksStatus() {
        boolean isRunning = scheduledTaskService.isRunning();

        Map<String, Object> response = new HashMap<>(2);
        response.put("code", "SUCCESS");
        response.put("running", isRunning);

        return ResponseEntity.ok(response);
    }
}