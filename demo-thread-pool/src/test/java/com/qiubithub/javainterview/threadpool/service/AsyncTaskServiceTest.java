package com.qiubithub.javainterview.threadpool.service;

import com.qiubithub.threadpool.ThreadPoolApplication;
import com.qiubithub.threadpool.service.AsyncTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 异步任务服务测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest(classes = ThreadPoolApplication.class)
@ComponentScan(basePackages = {"com.qiubithub.threadpool", "com.qiubithub.javainterview.threadpool"})
public class AsyncTaskServiceTest {

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Test
    @DisplayName("测试IO密集型异步任务")
    void testExecuteIoTask() throws ExecutionException, InterruptedException {
        Future<String> future = asyncTaskService.executeIoTask("io-task-1");
        
        // 等待任务完成
        String result = future.get();
        
        assertNotNull(result);
        assertTrue(result.contains("IO密集型任务"));
        assertTrue(result.contains("io-task-1"));
    }

    @Test
    @DisplayName("测试CPU密集型异步任务")
    void testExecuteCpuTask() throws ExecutionException, InterruptedException {
        Future<String> future = asyncTaskService.executeCpuTask("cpu-task-1");
        
        // 等待任务完成
        String result = future.get();
        
        assertNotNull(result);
        assertTrue(result.contains("CPU密集型任务"));
        assertTrue(result.contains("cpu-task-1"));
    }

    @Test
    @DisplayName("测试混合型异步任务")
    void testExecuteMixedTask() throws ExecutionException, InterruptedException {
        Future<String> future = asyncTaskService.executeMixedTask("mixed-task-1");
        
        // 等待任务完成
        String result = future.get();
        
        assertNotNull(result);
        assertTrue(result.contains("混合型任务"));
        assertTrue(result.contains("mixed-task-1"));
    }

    @Test
    @DisplayName("测试多个IO密集型异步任务")
    void testMultipleIoTasks() throws InterruptedException {
        int taskCount = 10;
        List<Future<String>> futures = new ArrayList<>();
        
        // 提交多个任务
        for (int i = 0; i < taskCount; i++) {
            futures.add(asyncTaskService.executeIoTask("io-task-" + i));
        }
        
        // 等待所有任务完成
        boolean allDone = false;
        int timeout = 0;
        while (!allDone && timeout < 30) {
            allDone = futures.stream().allMatch(Future::isDone);
            if (!allDone) {
                TimeUnit.SECONDS.sleep(1);
                timeout++;
            }
        }
        
        // 验证所有任务都已完成
        assertTrue(allDone, "所有任务应该在30秒内完成");
        
        // 验证任务结果
        for (int i = 0; i < taskCount; i++) {
            try {
                String result = futures.get(i).get();
                assertNotNull(result);
                assertTrue(result.contains("IO密集型任务"));
                assertTrue(result.contains("io-task-" + i));
            } catch (ExecutionException e) {
                fail("任务执行失败: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("测试多个CPU密集型异步任务")
    void testMultipleCpuTasks() throws InterruptedException {
        int taskCount = 10;
        List<Future<String>> futures = new ArrayList<>();
        
        // 提交多个任务
        for (int i = 0; i < taskCount; i++) {
            futures.add(asyncTaskService.executeCpuTask("cpu-task-" + i));
        }
        
        // 等待所有任务完成
        boolean allDone = false;
        int timeout = 0;
        while (!allDone && timeout < 30) {
            allDone = futures.stream().allMatch(Future::isDone);
            if (!allDone) {
                TimeUnit.SECONDS.sleep(1);
                timeout++;
            }
        }
        
        // 验证所有任务都已完成
        assertTrue(allDone, "所有任务应该在30秒内完成");
        
        // 验证任务结果
        for (int i = 0; i < taskCount; i++) {
            try {
                String result = futures.get(i).get();
                assertNotNull(result);
                assertTrue(result.contains("CPU密集型任务"));
                assertTrue(result.contains("cpu-task-" + i));
            } catch (ExecutionException e) {
                fail("任务执行失败: " + e.getMessage());
            }
        }
    }
}