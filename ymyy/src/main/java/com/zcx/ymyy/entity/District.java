package com.zcx.ymyy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("district")
public class District {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("parent")
    private String parent;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

}