package com.qiubithub.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 位置文档类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations")
public class Location {

    /**
     * 位置ID
     */
    @Id
    private String id;

    /**
     * 位置名称
     */
    @Indexed
    private String name;

    /**
     * 位置类型：store-商店，office-办公室，warehouse-仓库，other-其他
     */
    @Indexed
    private String type;

    /**
     * 位置坐标
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint coordinates;

    /**
     * 地址
     */
    private String address;

    /**
     * 城市
     */
    @Indexed
    private String city;

    /**
     * 省/州
     */
    @Indexed
    private String state;

    /**
     * 国家
     */
    @Indexed
    private String country;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 营业时间
     */
    private Map<String, String> businessHours;

    /**
     * 设施
     */
    private String[] facilities;

    /**
     * 状态：active-活跃，inactive-非活跃
     */
    @Indexed
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;
}