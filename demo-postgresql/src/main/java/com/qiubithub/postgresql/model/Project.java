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
 * 项目实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目编码
     */
    private String code;

    /**
     * 所属部门ID
     */
    private Long departmentId;

    /**
     * 项目负责人ID
     */
    private Long managerId;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目开始日期
     */
    private LocalDate startDate;

    /**
     * 项目结束日期
     */
    private LocalDate endDate;

    /**
     * 项目状态：1-规划中，2-进行中，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 项目优先级：1-低，2-中，3-高
     */
    private Integer priority;

    /**
     * 项目预算
     */
    private java.math.BigDecimal budget;

    /**
     * 项目元数据，使用JSON类型存储
     */
    private PGobject metadata;

    /**
     * 项目标签，使用数组类型存储
     */
    private String[] tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}