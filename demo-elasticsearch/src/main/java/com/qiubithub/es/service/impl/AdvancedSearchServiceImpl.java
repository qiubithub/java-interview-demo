package com.qiubithub.es.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiubithub.es.model.Blog;
import com.qiubithub.es.model.Product;
import com.qiubithub.es.model.User;
import com.qiubithub.es.service.AdvancedSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 高级搜索服务实现类
 * 提供复杂查询、聚合分析等高级功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.es.enabled", havingValue = "true")
public class AdvancedSearchServiceImpl implements AdvancedSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Value("${app.es.indices.product:products}")
    private String productIndex;

    @Value("${app.es.indices.blog:blogs}")
    private String blogIndex;

    @Value("${app.es.indices.user:users}")
    private String userIndex;

    /**
     * 多字段组合搜索产品
     */
    @Override
    public Map<String, Object> searchProducts(String keyword, String category, Double minPrice, Double maxPrice, String brand, int page, int size) {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(productIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 构建布尔查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "name", "description")
                        .fuzziness("AUTO"));
            }

            // 分类过滤
            if (StringUtils.hasText(category)) {
                boolQuery.filter(QueryBuilders.termQuery("category.keyword", category));
            }

            // 品牌过滤
            if (StringUtils.hasText(brand)) {
                boolQuery.filter(QueryBuilders.termQuery("brand.keyword", brand));
            }

            // 价格范围过滤
            if (minPrice != null || maxPrice != null) {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
                if (minPrice != null) {
                    rangeQuery.gte(minPrice);
                }
                if (maxPrice != null) {
                    rangeQuery.lte(maxPrice);
                }
                boolQuery.filter(rangeQuery);
            }

            // 设置查询
            searchSourceBuilder.query(boolQuery);

            // 分页
            searchSourceBuilder.from(page * size);
            searchSourceBuilder.size(size);

            // 排序
            searchSourceBuilder.sort("price", SortOrder.ASC);

            // 高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("name");
            highlightBuilder.field("description");
            highlightBuilder.preTags("<em>");
            highlightBuilder.postTags("</em>");
            searchSourceBuilder.highlighter(highlightBuilder);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理结果
            return processSearchResponse(searchResponse);

        } catch (IOException e) {
            log.error("搜索产品时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "搜索产品时发生错误: " + e.getMessage(),
                    "total", 0,
                    "items", Collections.emptyList()
            );
        }
    }

    /**
     * 多字段组合搜索博客
     */
    @Override
    public Map<String, Object> searchBlogs(String keyword, String author, String category, List<String> tags, Boolean published, int page, int size) {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(blogIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 构建布尔查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "title", "content")
                        .fuzziness("AUTO"));
            }

            // 作者过滤
            if (StringUtils.hasText(author)) {
                boolQuery.filter(QueryBuilders.termQuery("author.keyword", author));
            }

            // 分类过滤
            if (StringUtils.hasText(category)) {
                boolQuery.filter(QueryBuilders.termQuery("category.keyword", category));
            }

            // 标签过滤
            if (tags != null && !tags.isEmpty()) {
                boolQuery.filter(QueryBuilders.termsQuery("tags.keyword", tags));
            }

            // 发布状态过滤
            if (published != null) {
                boolQuery.filter(QueryBuilders.termQuery("published", published));
            }

            // 设置查询
            searchSourceBuilder.query(boolQuery);

            // 分页
            searchSourceBuilder.from(page * size);
            searchSourceBuilder.size(size);

            // 排序
            searchSourceBuilder.sort("createdAt", SortOrder.DESC);

            // 高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.field("content");
            highlightBuilder.preTags("<em>");
            highlightBuilder.postTags("</em>");
            searchSourceBuilder.highlighter(highlightBuilder);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理结果
            return processSearchResponse(searchResponse);

        } catch (IOException e) {
            log.error("搜索博客时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "搜索博客时发生错误: " + e.getMessage(),
                    "total", 0,
                    "items", Collections.emptyList()
            );
        }
    }

    /**
     * 多字段组合搜索用户
     */
    @Override
    public Map<String, Object> searchUsers(String keyword, Integer minAge, Integer maxAge, String gender, List<String> tags, int page, int size) {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(userIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 构建布尔查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "username", "nickname", "email")
                        .fuzziness("AUTO"));
            }

            // 年龄范围过滤
            if (minAge != null || maxAge != null) {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
                if (minAge != null) {
                    rangeQuery.gte(minAge);
                }
                if (maxAge != null) {
                    rangeQuery.lte(maxAge);
                }
                boolQuery.filter(rangeQuery);
            }

            // 性别过滤
            if (StringUtils.hasText(gender)) {
                boolQuery.filter(QueryBuilders.termQuery("gender.keyword", gender));
            }

            // 标签过滤
            if (tags != null && !tags.isEmpty()) {
                boolQuery.filter(QueryBuilders.termsQuery("tags.keyword", tags));
            }

            // 设置查询
            searchSourceBuilder.query(boolQuery);

            // 分页
            searchSourceBuilder.from(page * size);
            searchSourceBuilder.size(size);

            // 排序
            searchSourceBuilder.sort("age", SortOrder.ASC);

            // 高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("username");
            highlightBuilder.field("nickname");
            highlightBuilder.preTags("<em>");
            highlightBuilder.postTags("</em>");
            searchSourceBuilder.highlighter(highlightBuilder);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理结果
            return processSearchResponse(searchResponse);

        } catch (IOException e) {
            log.error("搜索用户时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "搜索用户时发生错误: " + e.getMessage(),
                    "total", 0,
                    "items", Collections.emptyList()
            );
        }
    }

    /**
     * 产品聚合分析
     */
    @Override
    public Map<String, Object> productAggregations() {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(productIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 设置查询
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            // 设置聚合
            // 1. 按品牌分组
            TermsAggregationBuilder brandAgg = AggregationBuilders.terms("by_brand")
                    .field("brand.keyword")
                    .size(10);

            // 2. 按分类分组
            TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("by_category")
                    .field("category.keyword")
                    .size(10);

            // 3. 价格统计
            searchSourceBuilder.aggregation(AggregationBuilders.min("min_price").field("price"));
            searchSourceBuilder.aggregation(AggregationBuilders.max("max_price").field("price"));
            searchSourceBuilder.aggregation(AggregationBuilders.avg("avg_price").field("price"));

            // 添加聚合
            searchSourceBuilder.aggregation(brandAgg);
            searchSourceBuilder.aggregation(categoryAgg);

            // 不需要返回文档，只需要聚合结果
            searchSourceBuilder.size(0);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理聚合结果
            return processAggregationResponse(searchResponse);

        } catch (IOException e) {
            log.error("产品聚合分析时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "产品聚合分析时发生错误: " + e.getMessage()
            );
        }
    }

    /**
     * 博客聚合分析
     */
    @Override
    public Map<String, Object> blogAggregations() {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(blogIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 设置查询
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            // 设置聚合
            // 1. 按作者分组
            TermsAggregationBuilder authorAgg = AggregationBuilders.terms("by_author")
                    .field("author.keyword")
                    .size(10);

            // 2. 按分类分组
            TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("by_category")
                    .field("category.keyword")
                    .size(10);

            // 3. 按标签分组
            TermsAggregationBuilder tagsAgg = AggregationBuilders.terms("by_tags")
                    .field("tags.keyword")
                    .size(20);

            // 4. 按发布状态分组
            TermsAggregationBuilder publishedAgg = AggregationBuilders.terms("by_published")
                    .field("published");

            // 添加聚合
            searchSourceBuilder.aggregation(authorAgg);
            searchSourceBuilder.aggregation(categoryAgg);
            searchSourceBuilder.aggregation(tagsAgg);
            searchSourceBuilder.aggregation(publishedAgg);

            // 不需要返回文档，只需要聚合结果
            searchSourceBuilder.size(0);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理聚合结果
            return processAggregationResponse(searchResponse);

        } catch (IOException e) {
            log.error("博客聚合分析时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "博客聚合分析时发生错误: " + e.getMessage()
            );
        }
    }

    /**
     * 用户聚合分析
     */
    @Override
    public Map<String, Object> userAggregations() {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(userIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 设置查询
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            // 设置聚合
            // 1. 按性别分组
            TermsAggregationBuilder genderAgg = AggregationBuilders.terms("by_gender")
                    .field("gender.keyword");

            // 2. 按年龄段分组
            searchSourceBuilder.aggregation(AggregationBuilders.histogram("by_age")
                    .field("age")
                    .interval(10));

            // 3. 按标签分组
            TermsAggregationBuilder tagsAgg = AggregationBuilders.terms("by_tags")
                    .field("tags.keyword")
                    .size(20);

            // 4. 年龄统计
            searchSourceBuilder.aggregation(AggregationBuilders.min("min_age").field("age"));
            searchSourceBuilder.aggregation(AggregationBuilders.max("max_age").field("age"));
            searchSourceBuilder.aggregation(AggregationBuilders.avg("avg_age").field("age"));

            // 添加聚合
            searchSourceBuilder.aggregation(genderAgg);
            searchSourceBuilder.aggregation(tagsAgg);

            // 不需要返回文档，只需要聚合结果
            searchSourceBuilder.size(0);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理聚合结果
            return processAggregationResponse(searchResponse);

        } catch (IOException e) {
            log.error("用户聚合分析时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "用户聚合分析时发生错误: " + e.getMessage()
            );
        }
    }

    /**
     * 全文搜索（搜索所有类型）
     */
    @Override
    public Map<String, Object> fullTextSearch(String keyword, int page, int size) {
        try {
            // 创建多索引搜索请求
            SearchRequest searchRequest = new SearchRequest(productIndex, blogIndex, userIndex);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 设置查询
            if (StringUtils.hasText(keyword)) {
                searchSourceBuilder.query(QueryBuilders.queryStringQuery(keyword)
                        .field("name", 2.0f)
                        .field("title", 2.0f)
                        .field("username", 2.0f)
                        .field("nickname", 1.5f)
                        .field("description")
                        .field("content")
                        .field("email")
                        .defaultOperator(org.elasticsearch.index.query.Operator.OR));
            } else {
                searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            }

            // 分页
            searchSourceBuilder.from(page * size);
            searchSourceBuilder.size(size);

            // 高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("*");
            highlightBuilder.requireFieldMatch(false);
            highlightBuilder.preTags("<em>");
            highlightBuilder.postTags("</em>");
            searchSourceBuilder.highlighter(highlightBuilder);

            // 设置搜索源
            searchRequest.source(searchSourceBuilder);

            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理结果
            Map<String, Object> result = processSearchResponse(searchResponse);
            
            // 添加每个文档的类型信息
            List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    String index = (String) item.get("_index");
                    if (index.equals(productIndex)) {
                        item.put("_type", "product");
                    } else if (index.equals(blogIndex)) {
                        item.put("_type", "blog");
                    } else if (index.equals(userIndex)) {
                        item.put("_type", "user");
                    }
                }
            }
            
            return result;

        } catch (IOException e) {
            log.error("全文搜索时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "全文搜索时发生错误: " + e.getMessage(),
                    "total", 0,
                    "items", Collections.emptyList()
            );
        }
    }

    /**
     * 复杂搜索（高级DSL查询）
     */
    @Override
    public Map<String, Object> complexSearch(String query) {
        try {
            // 解析查询DSL
            Map<String, Object> queryMap = objectMapper.readValue(query, Map.class);
            
            // 确定索引
            String indexName = (String) queryMap.getOrDefault("index", productIndex);
            
            // 创建搜索请求
            SearchRequest searchRequest = new SearchRequest(indexName);
            
            // 设置搜索源
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.wrapperQuery(objectMapper.writeValueAsString(queryMap.get("query"))));
            searchRequest.source(searchSourceBuilder);
            
            // 执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            
            // 处理结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("took", searchResponse.getTook().getMillis());
            result.put("timed_out", searchResponse.isTimedOut());
            
            // 处理命中结果
            result.put("total", searchResponse.getHits().getTotalHits().value);
            
            List<Map<String, Object>> items = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, Object> item = new HashMap<>(hit.getSourceAsMap());
                item.put("_id", hit.getId());
                item.put("_score", hit.getScore());
                item.put("_index", hit.getIndex());
                
                // 处理高亮
                if (!hit.getHighlightFields().isEmpty()) {
                    Map<String, List<String>> highlights = new HashMap<>();
                    for (Map.Entry<String, HighlightField> entry : hit.getHighlightFields().entrySet()) {
                        List<String> fragments = new ArrayList<>();
                        for (Text fragment : entry.getValue().getFragments()) {
                            fragments.add(fragment.string());
                        }
                        highlights.put(entry.getKey(), fragments);
                    }
                    item.put("highlights", highlights);
                }
                
                items.add(item);
            }
            result.put("items", items);
            
            // 处理聚合
            if (searchResponse.getAggregations() != null) {
                Map<String, Object> aggregations = processAggregations(searchResponse.getAggregations());
                result.put("aggregations", aggregations);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("执行复杂搜索时发生错误", e);
            return Map.of(
                    "success", false,
                    "message", "执行复杂搜索时发生错误: " + e.getMessage()
            );
        }
    }

    /**
     * 处理搜索响应
     */
    private Map<String, Object> processSearchResponse(SearchResponse searchResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("took", searchResponse.getTook().getMillis());
        result.put("timed_out", searchResponse.isTimedOut());
        result.put("total", searchResponse.getHits().getTotalHits().value);
        
        List<Map<String, Object>> items = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> item = new HashMap<>(hit.getSourceAsMap());
            item.put("_id", hit.getId());
            item.put("_score", hit.getScore());
            item.put("_index", hit.getIndex());
            
            // 处理高亮
            if (!hit.getHighlightFields().isEmpty()) {
                Map<String, List<String>> highlights = new HashMap<>();
                for (Map.Entry<String, HighlightField> entry : hit.getHighlightFields().entrySet()) {
                    List<String> fragments = new ArrayList<>();
                    for (Text fragment : entry.getValue().getFragments()) {
                        fragments.add(fragment.string());
                    }
                    highlights.put(entry.getKey(), fragments);
                }
                item.put("highlights", highlights);
            }
            
            items.add(item);
        }
        result.put("items", items);
        
        return result;
    }

    /**
     * 处理聚合响应
     */
    private Map<String, Object> processAggregationResponse(SearchResponse searchResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("took", searchResponse.getTook().getMillis());
        
        // 处理聚合
        if (searchResponse.getAggregations() != null) {
            Map<String, Object> aggregations = processAggregations(searchResponse.getAggregations());
            result.put("aggregations", aggregations);
        }
        
        return result;
    }

    /**
     * 处理聚合结果
     */
    private Map<String, Object> processAggregations(Aggregations aggregations) {
        Map<String, Object> result = new HashMap<>();
        
        aggregations.forEach(aggregation -> {
            String name = aggregation.getName();
            
            if (aggregation instanceof Terms) {
                List<Map<String, Object>> buckets = new ArrayList<>();
                ((Terms) aggregation).getBuckets().forEach(bucket -> {
                    Map<String, Object> bucketMap = new HashMap<>();
                    bucketMap.put("key", bucket.getKeyAsString());
                    bucketMap.put("doc_count", bucket.getDocCount());
                    
                    // 处理子聚合
                    if (bucket.getAggregations() != null && bucket.getAggregations().asList().size() > 0) {
                        bucketMap.put("aggregations", processAggregations(bucket.getAggregations()));
                    }
                    
                    buckets.add(bucketMap);
                });
                result.put(name, buckets);
            } else if (aggregation instanceof Min) {
                result.put(name, ((Min) aggregation).getValue());
            } else if (aggregation instanceof Max) {
                result.put(name, ((Max) aggregation).getValue());
            } else if (aggregation instanceof Avg) {
                result.put(name, ((Avg) aggregation).getValue());
            } else {
                // 其他类型的聚合
                result.put(name, aggregation.toString());
            }
        });
        
        return result;
    }
} 