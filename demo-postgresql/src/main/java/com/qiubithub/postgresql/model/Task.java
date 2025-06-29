package com.qiubithub.postgresql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 任务类型：1-需求，2-开发，3-测试，4-设计，5-文档
     */
    private Integer type;

    /**
     * 任务状态：1-待处理，2-进行中，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 任务优先级：1-低，2-中，3-高，4-紧急
     */
    private Integer priority;

    /**
     * 任务创建者ID
     */
    private Long creatorId;

    /**
     * 任务负责人ID
     */
    private Long assigneeId;

    /**
     * 任务开始日期
     */
    private LocalDate startDate;

    /**
     * 任务截止日期
     */
    private LocalDate dueDate;

    /**
     * 任务完成日期
     */
    private LocalDate completedDate;

    /**
     * 任务预计工时（小时）
     */
    private Double estimatedHours;

    /**
     * 任务实际工时（小时）
     */
    private Double actualHours;

    /**
     * 任务标签，使用数组类型存储
     */
    private String[] tags;

    /**
     * 任务元数据，使用JSON类型存储
     */
    private PGobject metadata;

    /**
     * 父任务ID
     */
    private Long parentId;

    /**
     * 任务层级路径
     */
    private String path;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}