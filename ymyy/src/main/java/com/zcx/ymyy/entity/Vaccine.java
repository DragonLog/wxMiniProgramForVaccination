package com.zcx.ymyy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("vaccine")
public class Vaccine {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("price")
    private Float price;

    @TableField("manufacturer")
    private String manufacturer;

    @TableField("category")
    private String category;

    @TableField("detail")
    private String detail;

    @TableField("img_url")
    private String imgUrl;
}
