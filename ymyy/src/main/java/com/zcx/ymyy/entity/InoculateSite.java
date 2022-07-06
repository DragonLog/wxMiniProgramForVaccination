package com.zcx.ymyy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("inoculate_site")
public class InoculateSite {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("province_code")
    private String provinceCode;

    @TableField("city_code")
    private String cityCode;

    @TableField("district_code")
    private String districtCode;

    @TableField("address")
    private String address;

    @TableField("img_url")
    private String imgUrl;
}
