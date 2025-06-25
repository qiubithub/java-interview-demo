package com.qiubithub.es.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.es.mapper.BlogMapper;
import com.qiubithub.es.model.Blog;
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
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 博客服务类
 * 使用Elasticsearch原生API实现博客的CRUD和高级查询功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final BlogMapper blogMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.es.indices.blog:blogs}")
    private String blogIndex;

    /**
     * 保存博客
     * @param blog 博客对象
     */
    public void saveBlog(Blog blog) {
        try {
            IndexRequest request = new IndexRequest(blogIndex);
            request.id(blog.getId());
            request.source(objectMapper.writeValueAsString(blog), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("保存博客失败", e);
        }
    }

    /**
     * 批量保存博客
     * @param blogs 博客列表
     */
    public void batchSaveBlogs(List<Blog> blogs) {
        blogs.forEach(this::saveBlog);
    }

    /**
     * 根据ID获取博客
     * @param id 博客ID
     * @return 博客对象
     */
    public Blog getBlogById(String id) {
        try {
            GetRequest request = new GetRequest(blogIndex, id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return objectMapper.readValue(response.getSourceAsString(), Blog.class);
            }
        } catch (IOException e) {
            log.error("获取博客失败", e);
        }
        return null;
    }

    /**
     * 根据ID删除博客
     * @param id 博客ID
     */
    public void deleteBlog(String id) {
        try {
            DeleteRequest request = new DeleteRequest(blogIndex, id);
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除博客失败", e);
        }
    }

    /**
     * 获取所有博客
     * @return 博客列表
     */
    public List<Blog> getAllBlogs() {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("获取所有博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据标题查找博客
     * @param title 博客标题
     * @return 博客列表
     */
    public List<Blog> findByTitle(String title) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("title.keyword", title));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("根据标题查找博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据标题模糊查找博客
     * @param keyword 关键词
     * @return 博客列表
     */
    public List<Blog> findByTitleContaining(String keyword) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("title", keyword));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("根据标题模糊查找博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据内容模糊查找博客
     * @param keyword 关键词
     * @return 博客列表
     */
    public List<Blog> findByContentContaining(String keyword) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("content", keyword));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("根据内容模糊查找博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据作者查找博客
     * @param author 作者
     * @return 博客列表
     */
    public List<Blog> findByAuthor(String author) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("author.keyword", author));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("根据作者查找博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据分类查找博客
     * @param category 分类
     * @return 博客列表
     */
    public List<Blog> findByCategory(String category) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("category.keyword", category));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("根据分类查找博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 查找已发布的博客
     * @return 博客列表
     */
    public List<Blog> findPublishedBlogs() {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("published", true));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("查找已发布的博客失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 查找热门博客
     * @param limit 限制数量
     * @return 博客列表
     */
    public List<Blog> findHotBlogs(int limit) {
        try {
            SearchRequest request = new SearchRequest(blogIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("published", true));
            sourceBuilder.sort("viewCount", SortOrder.DESC);
            sourceBuilder.size(limit);
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractBlogsFromResponse(response);
        } catch (IOException e) {
            log.error("查找热门博客失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 从响应中提取博客列表
     */
    private List<Blog> extractBlogsFromResponse(SearchResponse response) {
        List<Blog> blogs = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            try {
                Blog blog = objectMapper.readValue(hit.getSourceAsString(), Blog.class);
                blog.setId(hit.getId());
                blogs.add(blog);
            } catch (IOException e) {
                log.error("解析博客数据失败", e);
            }
        }
        return blogs;
    }
}