package com.zcx.ymyy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("pre_check")
public class PreCheck {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("is_medicine")
    private Integer isMedicine;

    @TableField("medicine")
    private String medicine;

    @TableField("is_contraindication")
    private Integer isContraindication;

    @TableField("contraindication")
    private String contraindication;

    @TableField("blood_pressure_high")
    private Float bloodPressureHigh;

    @TableField("blood_pressure_low")
    private Float bloodPressureLow;

    @TableField("temperature")
    private Float temperature;

    @TableField("worker_id")
    private Integer workerId;

    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField("appoint_id")
    private Integer appointId;

    @TableField("note")
    private String note;

}
