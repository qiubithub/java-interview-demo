package com.qiubithub.ddd.domain.model.valueobject;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 地址值对象
 */
@Getter
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    private String province;
    private String city;
    private String district;
    private String street;
    private String detail;
    private String zipCode;

    protected Address() {
        // JPA需要无参构造函数
    }

    public Address(String province, String city, String district, String street, String detail, String zipCode) {
        // 省市区街道不能为空
        if (StringUtils.isBlank(province)) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (StringUtils.isBlank(city)) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (StringUtils.isBlank(district)) {
            throw new IllegalArgumentException("区县不能为空");
        }
        if (StringUtils.isBlank(street)) {
            throw new IllegalArgumentException("街道不能为空");
        }
        
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    /**
     * 获取完整地址字符串
     *
     * @return 格式化的地址字符串
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(province).append(city).append(district).append(street);
        if (StringUtils.isNotBlank(detail)) {
            sb.append(" ").append(detail);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(province, address.province) &&
                Objects.equals(city, address.city) &&
                Objects.equals(district, address.district) &&
                Objects.equals(street, address.street) &&
                Objects.equals(detail, address.detail) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(province, city, district, street, detail, zipCode);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}