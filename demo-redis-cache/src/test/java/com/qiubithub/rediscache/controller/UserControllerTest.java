package com.qiubithub.rediscache.controller;

import com.qiubithub.rediscache.model.CommonResult;
import com.qiubithub.rediscache.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 用户控制器测试类
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("测试获取所有用户")
    void testGetAllUsers() {
        String url = "http://localhost:" + port + "/redis-cache/api/users";
        
        ResponseEntity<CommonResult<List<User>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResult<List<User>>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertFalse(response.getBody().getData().isEmpty());
    }

    @Test
    @DisplayName("测试根据ID获取用户")
    void testGetUserById() {
        String url = "http://localhost:" + port + "/redis-cache/api/users/1";
        
        ResponseEntity<CommonResult<User>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("admin", response.getBody().getData().getUsername());
    }

    @Test
    @DisplayName("测试创建用户")
    void testCreateUser() {
        String url = "http://localhost:" + port + "/redis-cache/api/users";
        
        User newUser = new User();
        newUser.setUsername("apiTest");
        newUser.setPassword("apiTest123");
        newUser.setEmail("api@example.com");
        newUser.setPhone("13800138007");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<User> request = new HttpEntity<>(newUser, headers);
        
        ResponseEntity<CommonResult<User>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertNotNull(response.getBody().getData().getId());
        assertEquals("apiTest", response.getBody().getData().getUsername());
        assertEquals("api@example.com", response.getBody().getData().getEmail());
    }

    @Test
    @DisplayName("测试更新用户")
    void testUpdateUser() {
        // 先创建一个用户
        String createUrl = "http://localhost:" + port + "/redis-cache/api/users";
        
        User newUser = new User();
        newUser.setUsername("updateApiTest");
        newUser.setPassword("updateApiTest123");
        newUser.setEmail("updateApi@example.com");
        newUser.setPhone("13800138008");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<User> createRequest = new HttpEntity<>(newUser, headers);
        
        ResponseEntity<CommonResult<User>> createResponse = restTemplate.exchange(
                createUrl,
                HttpMethod.POST,
                createRequest,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        Long userId = createResponse.getBody().getData().getId();
        
        // 更新用户
        String updateUrl = "http://localhost:" + port + "/redis-cache/api/users/" + userId;
        
        User updateData = new User();
        updateData.setUsername("updatedApiName");
        updateData.setEmail("updatedApi@example.com");
        
        HttpEntity<User> updateRequest = new HttpEntity<>(updateData, headers);
        
        ResponseEntity<CommonResult<User>> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                updateRequest,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        assertTrue(updateResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(updateResponse.getBody());
        assertEquals(200, updateResponse.getBody().getCode());
        assertNotNull(updateResponse.getBody().getData());
        assertEquals(userId, updateResponse.getBody().getData().getId());
        assertEquals("updatedApiName", updateResponse.getBody().getData().getUsername());
        assertEquals("updatedApi@example.com", updateResponse.getBody().getData().getEmail());
    }

    @Test
    @DisplayName("测试删除用户")
    void testDeleteUser() {
        // 先创建一个用户
        String createUrl = "http://localhost:" + port + "/redis-cache/api/users";
        
        User newUser = new User();
        newUser.setUsername("deleteApiTest");
        newUser.setPassword("deleteApiTest123");
        newUser.setEmail("deleteApi@example.com");
        newUser.setPhone("13800138009");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<User> createRequest = new HttpEntity<>(newUser, headers);
        
        ResponseEntity<CommonResult<User>> createResponse = restTemplate.exchange(
                createUrl,
                HttpMethod.POST,
                createRequest,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        Long userId = createResponse.getBody().getData().getId();
        
        // 删除用户
        String deleteUrl = "http://localhost:" + port + "/redis-cache/api/users/" + userId;
        
        ResponseEntity<CommonResult<String>> deleteResponse = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<CommonResult<String>>() {}
        );
        
        assertTrue(deleteResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(deleteResponse.getBody());
        assertEquals(200, deleteResponse.getBody().getCode());
        assertEquals("删除成功", deleteResponse.getBody().getData());
        
        // 验证用户已删除
        ResponseEntity<CommonResult<User>> getResponse = restTemplate.exchange(
                "http://localhost:" + port + "/redis-cache/api/users/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CommonResult<User>>() {}
        );
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(404, getResponse.getBody().getCode());
        assertEquals("用户不存在", getResponse.getBody().getMessage());
    }
}