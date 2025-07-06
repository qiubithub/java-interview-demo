package com.qiubithub.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论文档类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    /**
     * 评论ID
     */
    @Id
    private String id;

    /**
     * 内容ID
     */
    @Indexed
    private String contentId;

    /**
     * 内容类型
     */
    @Indexed
    private String contentType;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者ID
     */
    @Indexed
    private String userId;

    /**
     * 评论者名称
     */
    private String userName;

    /**
     * 评论者头像
     */
    private String userAvatar;

    /**
     * 父评论ID
     */
    @Indexed
    private String parentId;

    /**
     * 根评论ID
     */
    @Indexed
    private String rootId;

    /**
     * 回复的评论ID
     */
    private String replyToId;

    /**
     * 回复的用户ID
     */
    private String replyToUserId;

    /**
     * 回复的用户名称
     */
    private String replyToUserName;

    /**
     * 点赞数
     */
    private Integer likesCount;

    /**
     * 点踩数
     */
    private Integer dislikesCount;

    /**
     * 回复数
     */
    private Integer repliesCount;

    /**
     * 评论层级
     */
    private Integer level;

    /**
     * 评论路径
     */
    private String path;

    /**
     * 评论状态：pending-待审核，approved-已通过，rejected-已拒绝，deleted-已删除
     */
    @Indexed
    private String status;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 评论图片
     */
    private List<String> images;

    /**
     * 创建时间
     */
    @Indexed
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
     * 是否精选
     */
    @Indexed
    private Boolean isFeatured;

    /**
     * 标签
     */
    private List<String> tags;
}