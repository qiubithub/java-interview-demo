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
 * 产品实体类
 * 使用Spring Data Elasticsearch注解进行索引映射
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class Product {

    /**
     * 产品ID
     */
    @Id
    private String id;

    /**
     * 产品名称
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * 产品描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /**
     * 产品价格
     */
    @Field(type = FieldType.Double)
    private Double price;

    /**
     * 产品分类
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * 产品品牌
     */
    @Field(type = FieldType.Keyword)
    private String brand;

    /**
     * 产品标签
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 产品库存
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

    /**
     * 产品销量
     */
    @Field(type = FieldType.Integer)
    private Integer sales;

    /**
     * 产品评分
     */
    @Field(type = FieldType.Float)
    private Float rating;

    /**
     * 产品评论数
     */
    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    /**
     * 产品状态（上架、下架）
     */
    @Field(type = FieldType.Boolean)
    private Boolean status;

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