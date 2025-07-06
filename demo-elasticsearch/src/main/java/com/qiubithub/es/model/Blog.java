package com.qiubithub.es.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客实体类
 * 使用Spring Data Elasticsearch注解进行索引映射
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "blogs") // 指定索引名称
public class Blog {

    /**
     * 博客ID
     */
    @Id
    private String id;

    /**
     * 博客标题
     */
    @Field(type = FieldType.Keyword)
    private String title;

    /**
     * 博客内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 作者
     */
    @Field(type = FieldType.Keyword)
    private String author;

    /**
     * 博客分类
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * 博客标签
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 是否发布
     */
    @Field(type = FieldType.Boolean)
    private Boolean published;

    /**
     * 浏览次数
     */
    @Field(type = FieldType.Integer)
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Field(type = FieldType.Integer)
    private Integer likeCount;

    /**
     * 发布日期
     */
    @Field(type = FieldType.Date)
    private LocalDateTime publishDate;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updateTime;
}