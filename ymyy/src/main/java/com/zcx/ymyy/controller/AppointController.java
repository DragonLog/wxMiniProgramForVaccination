package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.exception.TokenUnavailable;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.entity.Result;
import com.zcx.ymyy.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class AppointController {

    @Autowired
    private AppointService appointService;

    @Autowired
    private UserService userService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private PlanService planService;

    @Autowired
    private VaccineService vaccineService;

    @PassToken(required = false)
    @GetMapping("/admin/appoint/pageQuery")
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<Appoint> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                appointQueryWrapper.eq("user_id", null);
            } else {
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
            }
        }

        page = appointService.page(page, appointQueryWrapper);
        List<Appoint> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            User user = userService.getById(records.get(i).getUserId());
            result.put("realName", user.getRealName());
            result.put("appointDate", new SimpleDateFormat("yyyy-MM-dd").format(records.get(i).getAppointDate()));
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            result.put("qrCodeUrl", records.get(i).getQrCodeUrl());
            Plan plan = planService.getById(records.get(i).getPlanId());
            if (records.get(i).getTimeSlot() == 0) {
                result.put("timeSlot", plan.getStartTimeMorning() + "??? - " + plan.getEndTimeMorning() + "???");
            } else if (records.get(i).getTimeSlot() == 1) {
                result.put("timeSlot", plan.getStartTimeAfternoon() + "??? - " + plan.getEndTimeAfternoon() + "???");
            } else {
                result.put("timeSlot", plan.getStartTimeEvening() + "??? - " + plan.getEndTimeEvening() + "???");
            }
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            if (records.get(i).getStatus() == 0) {
                result.put("status", "?????????");
            } else if (records.get(i).getStatus() == 1) {
                result.put("status", "?????????");
            } else if (records.get(i).getStatus() == 2) {
                result.put("status", "?????????");
            } else if (records.get(i).getStatus() == 3) {
                result.put("status", "?????????");
            } else if (records.get(i).getStatus() == 4) {
                result.put("status", "????????????");
            } else if (records.get(i).getStatus() == 5) {
                result.put("status", "????????????");
            } else if (records.get(i).getStatus() == 6) {
                result.put("status", "????????????");
            } else {
                result.put("status", "??????????????????");
            }
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PostMapping("/user/appoint/ok")
    @PassToken(required = false)
    public Result ok(@RequestBody Appoint appoint, @RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token??????");
        }

        if (appoint.getPlanId() == null) {
            return Result.error("????????????ID????????????");
        }
        if (appoint.getAppointDate() == null) {
            return Result.error("????????????????????????");
        }
        if (appoint.getTimeSlot() == null) {
            return Result.error("???????????????????????????");
        }
        Plan plan = planService.getById(appoint.getPlanId());
        if (plan == null) {
            return Result.error("?????????????????????");
        }
        Date now = new Date();
        if (now.after(plan.getEndDate())) {
            return Result.error("??????????????????????????????????????????????????????");
        }
        if (!(appoint.getAppointDate().after(now) && appoint.getAppointDate().before(new Date(plan.getEndDate().getTime() + 1000 * 60 * 60 * 24 * 1)))) {
            return Result.error("???????????????????????????????????????????????????");
        }
        if (plan.getAmount() < 1) {
            return Result.error("??????????????????");
        }
        if (appoint.getTimeSlot() != 1 && appoint.getTimeSlot() != 0 && appoint.getTimeSlot() != 2) {
            return Result.error("?????????????????????");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        if (appoint.getTimeSlot() == 0) {
            appointQueryWrapper.ne("status", 6);
            if (appoint.getAppointDate() != null) {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
            } else {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
            }
            appointQueryWrapper.eq("time_slot", 0);
            appointQueryWrapper.eq("plan_id", plan.getId());
            if (plan.getMorningLimit() - appointService.count(appointQueryWrapper) < 1) {
                return Result.error("??????????????????????????????");
            }
        } else if (appoint.getTimeSlot() == 1) {
            appointQueryWrapper.ne("status", 6);
            if (appoint.getAppointDate() != null) {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
            } else {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
            }
            appointQueryWrapper.eq("time_slot", 1);
            appointQueryWrapper.eq("plan_id", plan.getId());
            if(plan.getAfternoonLimit() - appointService.count(appointQueryWrapper) < 1) {
                return Result.error("??????????????????????????????");
            }
        } else {
            appointQueryWrapper.ne("status", 6);
            if (appoint.getAppointDate() != null) {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
            } else {
                appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
            }
            appointQueryWrapper.eq("time_slot", 2);
            appointQueryWrapper.eq("plan_id", plan.getId());
            if (plan.getEveningLimit() - appointService.count(appointQueryWrapper) < 1) {
                return Result.error("??????????????????????????????");
            }
        }

        appointQueryWrapper.clear();;
        appointQueryWrapper.eq("status", 0);
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> list = appointService.list(appointQueryWrapper);
        if (list.size() != 0) {
            return Result.error("???????????????????????????");
        }
        return Result.ok("????????????");
    }

}

