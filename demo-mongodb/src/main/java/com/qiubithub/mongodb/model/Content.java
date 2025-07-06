package com.qiubithub.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容文档类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contents")
public class Content {

    /**
     * 内容ID
     */
    @Id
    private String id;

    /**
     * 内容标题
     */
    @TextIndexed(weight = 3)
    @Indexed
    private String title;

    /**
     * 内容摘要
     */
    @TextIndexed(weight = 2)
    private String summary;

    /**
     * 内容正文
     */
    @TextIndexed(weight = 1)
    private String body;

    /**
     * 内容类型：article-文章，blog-博客，news-新闻，page-页面
     */
    @Indexed
    private String type;

    /**
     * 内容状态：draft-草稿，published-已发布，archived-已归档
     */
    @Indexed
    private String status;

    /**
     * 作者ID
     */
    @Indexed
    private String authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 标签
     */
    @Indexed
    private List<String> tags;

    /**
     * 分类
     */
    @Indexed
    private List<String> categories;

    /**
     * SEO关键词
     */
    private List<String> keywords;

    /**
     * SEO描述
     */
    private String seoDescription;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 点赞数
     */
    private Integer likesCount;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 浏览数
     */
    private Integer viewsCount;

    /**
     * 分享数
     */
    private Integer sharesCount;

    /**
     * 发布时间
     */
    @Indexed
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否置顶
     */
    @Indexed
    private Boolean isSticky;

    /**
     * 是否推荐
     */
    @Indexed
    private Boolean isRecommended;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 原文链接
     */
    private String originalUrl;

    /**
     * 相关内容ID列表
     */
    private List<String> relatedContentIds;
}