package com.qiubithub.es.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.es.mapper.UserMapper;
import com.qiubithub.es.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户服务类
 * 使用Elasticsearch原生API实现用户的CRUD和高级查询功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.es.indices.user:users}")
    private String userIndex;

    /**
     * 保存用户
     * @param user 用户对象
     */
    public void saveUser(User user) {
        try {
            IndexRequest request = new IndexRequest(userIndex);
            request.id(user.getId());
            request.source(objectMapper.writeValueAsString(user), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("保存用户失败", e);
        }
    }

    /**
     * 批量保存用户
     * @param users 用户列表
     */
    public void batchSaveUsers(List<User> users) {
        users.forEach(this::saveUser);
            }

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    public User getUserById(String id) {
        try {
            GetRequest request = new GetRequest(userIndex, id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return objectMapper.readValue(response.getSourceAsString(), User.class);
            }
        } catch (IOException e) {
            log.error("获取用户失败", e);
        }
        return null;
    }

    /**
     * 根据ID删除用户
     * @param id 用户ID
     */
    public void deleteUser(String id) {
        try {
            DeleteRequest request = new DeleteRequest(userIndex, id);
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除用户失败", e);
        }
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        try {
            SearchRequest request = new SearchRequest(userIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractUsersFromResponse(response);
        } catch (IOException e) {
            log.error("获取所有用户失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户列表
     */
    public List<User> findByUsername(String username) {
        try {
            SearchRequest request = new SearchRequest(userIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("username.keyword", username));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractUsersFromResponse(response);
        } catch (IOException e) {
            log.error("根据用户名查找用户失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户列表
     */
    public List<User> findByEmail(String email) {
        try {
            SearchRequest request = new SearchRequest(userIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("email.keyword", email));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractUsersFromResponse(response);
        } catch (IOException e) {
            log.error("根据邮箱查找用户失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据年龄范围查找用户
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 用户列表
     */
    public List<User> findByAgeRange(int minAge, int maxAge) {
        try {
            SearchRequest request = new SearchRequest(userIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.rangeQuery("age").gte(minAge).lte(maxAge));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractUsersFromResponse(response);
        } catch (IOException e) {
            log.error("根据年龄范围查找用户失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据性别查找用户
     * @param gender 性别
     * @return 用户列表
     */
    public List<User> findByGender(String gender) {
        try {
            SearchRequest request = new SearchRequest(userIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("gender.keyword", gender));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractUsersFromResponse(response);
        } catch (IOException e) {
            log.error("根据性别查找用户失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 从响应中提取用户列表
     */
    private List<User> extractUsersFromResponse(SearchResponse response) {
        List<User> users = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            try {
                User user = objectMapper.readValue(hit.getSourceAsString(), User.class);
                user.setId(hit.getId());
                users.add(user);
            } catch (IOException e) {
                log.error("解析用户数据失败", e);
        }
        }
        return users;
    }
}