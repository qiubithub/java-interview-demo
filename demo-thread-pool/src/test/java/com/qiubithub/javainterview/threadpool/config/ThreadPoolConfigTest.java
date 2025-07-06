package com.qiubithub.javainterview.threadpool.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 线程池配置测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
public class ThreadPoolConfigTest {

    @Autowired
    @Qualifier("ioThreadPool")
    private Executor ioThreadPool;

    @Autowired
    @Qualifier("cpuThreadPool")
    private Executor cpuThreadPool;

    @Autowired
    @Qualifier("mixedThreadPool")
    private Executor mixedThreadPool;

    @Test
    @DisplayName("测试IO密集型线程池")
    void testIoThreadPool() {
        assertNotNull(ioThreadPool);
        assertTrue(ioThreadPool instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) ioThreadPool;
        
        // 验证线程池配置
        int cpuCount = Runtime.getRuntime().availableProcessors();
        assertEquals(cpuCount * 2, executor.getCorePoolSize());
        assertEquals(cpuCount * 4, executor.getMaxPoolSize());
        assertEquals(500, executor.getQueueCapacity());
    }

    @Test
    @DisplayName("测试CPU密集型线程池")
    void testCpuThreadPool() {
        assertNotNull(cpuThreadPool);
        assertTrue(cpuThreadPool instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) cpuThreadPool;
        
        // 验证线程池配置
        int cpuCount = Runtime.getRuntime().availableProcessors();
        assertEquals(cpuCount + 1, executor.getCorePoolSize());
        assertEquals((cpuCount + 1) * 2, executor.getMaxPoolSize());
        assertEquals(200, executor.getQueueCapacity());
    }

    @Test
    @DisplayName("测试混合型线程池")
    void testMixedThreadPool() {
        assertNotNull(mixedThreadPool);
        assertTrue(mixedThreadPool instanceof ThreadPoolTaskExecutor);
        
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) mixedThreadPool;
        
        // 验证线程池配置
        assertEquals(10, executor.getCorePoolSize());
        assertEquals(20, executor.getMaxPoolSize());
        assertEquals(300, executor.getQueueCapacity());
    }

    @Test
    @DisplayName("测试IO密集型线程池执行任务")
    void testIoThreadPoolExecution() throws InterruptedException {
        int taskCount = 20;
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Future<String>> futures = new ArrayList<>();
        
        // 提交多个任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            futures.add(((ThreadPoolTaskExecutor) ioThreadPool).submit(() -> {
                try {
                    Thread.sleep(100);  // 模拟IO操作
                    return "IO任务 " + taskId + " 完成，线程: " + Thread.currentThread().getName();
                } finally {
                    latch.countDown();
                }
            }));
        }
        
        // 等待所有任务完成
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应该在10秒内完成");
        
        // 验证所有任务结果
        for (int i = 0; i < taskCount; i++) {
            try {
                String result = futures.get(i).get();
                assertNotNull(result);
                assertTrue(result.contains("IO任务 " + i + " 完成"));
                assertTrue(result.contains("io-task-"));
            } catch (ExecutionException e) {
                fail("任务执行失败: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("测试CPU密集型线程池执行任务")
    void testCpuThreadPoolExecution() throws InterruptedException {
        int taskCount = 20;
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Future<String>> futures = new ArrayList<>();
        
        // 提交多个任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            futures.add(((ThreadPoolTaskExecutor) cpuThreadPool).submit(() -> {
                try {
                    // 模拟CPU密集型计算
                    long result = 0;
                    for (int j = 0; j < 1000000; j++) {
                        result += j;
                    }
                    return "CPU任务 " + taskId + " 完成，线程: " + Thread.currentThread().getName() + ", 结果: " + result;
                } finally {
                    latch.countDown();
                }
            }));
        }
        
        // 等待所有任务完成
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应该在10秒内完成");
        
        // 验证所有任务结果
        for (int i = 0; i < taskCount; i++) {
            try {
                String result = futures.get(i).get();
                assertNotNull(result);
                assertTrue(result.contains("CPU任务 " + i + " 完成"));
                assertTrue(result.contains("cpu-task-"));
            } catch (ExecutionException e) {
                fail("任务执行失败: " + e.getMessage());
            }
        }
    }
}