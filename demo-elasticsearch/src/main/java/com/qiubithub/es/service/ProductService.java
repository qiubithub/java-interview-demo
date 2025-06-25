package com.qiubithub.es.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.es.mapper.ProductMapper;
import com.qiubithub.es.model.Product;
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
 * 产品服务类
 * 使用Elasticsearch原生API实现产品的CRUD和高级查询功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductMapper productMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.es.indices.product:products}")
    private String productIndex;

    /**
     * 保存产品
     * @param product 产品对象
     */
    public void saveProduct(Product product) {
        try {
            IndexRequest request = new IndexRequest(productIndex);
            request.id(product.getId());
            request.source(objectMapper.writeValueAsString(product), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("保存产品失败", e);
        }
    }

    /**
     * 批量保存产品
     * @param products 产品列表
     */
    public void batchSaveProducts(List<Product> products) {
        products.forEach(this::saveProduct);
    }

    /**
     * 根据ID获取产品
     * @param id 产品ID
     * @return 产品对象
     */
    public Product getProductById(String id) {
        try {
            GetRequest request = new GetRequest(productIndex, id);
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return objectMapper.readValue(response.getSourceAsString(), Product.class);
            }
        } catch (IOException e) {
            log.error("获取产品失败", e);
        }
        return null;
    }

    /**
     * 根据ID删除产品
     * @param id 产品ID
     */
    public void deleteProduct(String id) {
        try {
            DeleteRequest request = new DeleteRequest(productIndex, id);
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除产品失败", e);
        }
    }

    /**
     * 获取所有产品
     * @return 产品列表
     */
    public List<Product> getAllProducts() {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("获取所有产品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据名称查找产品
     * @param name 产品名称
     * @return 产品列表
     */
    public List<Product> findByName(String name) {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("name.keyword", name));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("根据名称查找产品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据名称模糊查找产品
     * @param keyword 关键词
     * @return 产品列表
     */
    public List<Product> findByNameContaining(String keyword) {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("根据名称模糊查找产品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据分类查找产品
     * @param category 分类
     * @return 产品列表
     */
    public List<Product> findByCategory(String category) {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("category.keyword", category));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("根据分类查找产品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据价格范围查找产品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 产品列表
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("根据价格范围查找产品失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据品牌查找产品
     * @param brand 品牌
     * @return 产品列表
     */
    public List<Product> findByBrand(String brand) {
        try {
            SearchRequest request = new SearchRequest(productIndex);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("brand.keyword", brand));
            request.source(sourceBuilder);
            
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return extractProductsFromResponse(response);
        } catch (IOException e) {
            log.error("根据品牌查找产品失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 从响应中提取产品列表
     */
    private List<Product> extractProductsFromResponse(SearchResponse response) {
        List<Product> products = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            try {
                Product product = objectMapper.readValue(hit.getSourceAsString(), Product.class);
                product.setId(hit.getId());
                products.add(product);
            } catch (IOException e) {
                log.error("解析产品数据失败", e);
            }
        }
        return products;
    }
}