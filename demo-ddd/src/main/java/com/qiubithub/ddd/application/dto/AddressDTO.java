package com.qiubithub.ddd.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    
    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;
    
    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;
    
    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;
    
    /**
     * 街道
     */
    @NotBlank(message = "街道不能为空")
    private String street;
    
    /**
     * 详细地址
     */
    private String detail;
    
    /**
     * 邮政编码
     */
    private String zipCode;
}