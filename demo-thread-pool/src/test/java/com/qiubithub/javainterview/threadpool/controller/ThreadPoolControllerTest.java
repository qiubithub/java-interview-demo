package com.qiubithub.javainterview.threadpool.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <p>
 * 线程池控制器测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ThreadPoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试执行IO密集型任务")
    void testExecuteIoTask() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/thread-pool/io")
                .param("taskId", "test-io-task")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("IO任务响应: " + content);
    }

    @Test
    @DisplayName("测试执行CPU密集型任务")
    void testExecuteCpuTask() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/thread-pool/cpu")
                .param("taskId", "test-cpu-task")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("CPU任务响应: " + content);
    }

    @Test
    @DisplayName("测试执行混合型任务")
    void testExecuteMixedTask() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/thread-pool/mixed")
                .param("taskId", "test-mixed-task")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("混合任务响应: " + content);
    }

    @Test
    @DisplayName("测试获取任务结果")
    void testGetTaskResult() throws Exception {
        // 先提交一个任务
        MvcResult submitResult = mockMvc.perform(get("/api/thread-pool/io")
                .param("taskId", "task-for-result")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String taskId = submitResult.getResponse().getContentAsString().split("\"data\":\"")[1].split("\"")[0];
        
        // 等待任务完成
        TimeUnit.SECONDS.sleep(3);
        
        // 获取任务结果
        MvcResult result = mockMvc.perform(get("/api/thread-pool/result")
                .param("taskId", taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("任务结果响应: " + content);
    }

    @Test
    @DisplayName("测试使用分布式锁处理业务")
    void testProcessBusinessWithLock() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/thread-pool/business/lock")
                .param("businessId", "test-business-lock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data", containsString("使用分布式锁")))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("使用锁处理业务响应: " + content);
    }

    @Test
    @DisplayName("测试不使用分布式锁处理业务")
    void testProcessBusinessWithoutLock() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/thread-pool/business/nolock")
                .param("businessId", "test-business-nolock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data", containsString("不使用分布式锁")))
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        System.out.println("不使用锁处理业务响应: " + content);
    }

    @Test
    @DisplayName("测试并发使用分布式锁处理业务")
    void testConcurrentProcessBusinessWithLock() throws Exception {
        // 模拟并发请求，这里简单地连续发送两个请求
        String businessId = "concurrent-lock-test";
        
        // 第一个请求
        MvcResult result1 = mockMvc.perform(get("/api/thread-pool/business/lock")
                .param("businessId", businessId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // 立即发送第二个请求，模拟并发
        MvcResult result2 = mockMvc.perform(get("/api/thread-pool/business/lock")
                .param("businessId", businessId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String content1 = result1.getResponse().getContentAsString();
        String content2 = result2.getResponse().getContentAsString();
        
        System.out.println("并发请求1响应: " + content1);
        System.out.println("并发请求2响应: " + content2);
        
        // 至少有一个请求应该返回成功，另一个可能返回系统繁忙
        assertTrue(content1.contains("处理成功") || content2.contains("处理成功"));
    }
}