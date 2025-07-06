package com.qiubithub.postgresql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 部门层级路径，例如：1,2,3
     */
    private String path;

    /**
     * 部门层级
     */
    private Integer level;

    /**
     * 部门排序
     */
    private Integer sort;

    /**
     * 部门负责人ID
     */
    private Long leaderId;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 部门状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}