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
@TableName("plan")
public class Plan {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @TableField("start_date")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @TableField("end_date")
    private Date endDate;

    @TableField("start_time_morning")
    private Integer startTimeMorning;

    @TableField("end_time_morning")
    private Integer endTimeMorning;

    @TableField("end_time_afternoon")
    private Integer endTimeAfternoon;

    @TableField("start_time_afternoon")
    private Integer startTimeAfternoon;

    @TableField("vaccine_id")
    private Integer vaccineId;

    @TableField("inoculate_site_id")
    private Integer inoculateSiteId;

    @TableField("amount")
    private Integer amount;

    @TableField("start_time_evening")
    private Integer startTimeEvening;

    @TableField("end_time_evening")
    private Integer endTimeEvening;

    @TableField("morning_limit")
    private Integer morningLimit;

    @TableField("afternoon_limit")
    private Integer afternoonLimit;

    @TableField("evening_limit")
    private Integer eveningLimit;

}
