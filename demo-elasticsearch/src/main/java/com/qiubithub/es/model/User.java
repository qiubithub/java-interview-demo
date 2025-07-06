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
 * 用户实体类
 * 使用Spring Data Elasticsearch注解进行索引映射
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "users")
public class User {

    /**
     * 用户ID
     */
    @Id
    private String id;

    /**
     * 用户名
     */
    @Field(type = FieldType.Keyword)
    private String username;

    /**
     * 邮箱
     */
    @Field(type = FieldType.Keyword)
    private String email;

    /**
     * 昵称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String nickname;

    /**
     * 密码
     */
    @Field(type = FieldType.Keyword)
    private String password;

    /**
     * 年龄
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * 性别
     */
    @Field(type = FieldType.Keyword)
    private String gender;

    /**
     * 地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String address;

    /**
     * 电话号码
     */
    @Field(type = FieldType.Keyword)
    private String phone;

    /**
     * 用户标签
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

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