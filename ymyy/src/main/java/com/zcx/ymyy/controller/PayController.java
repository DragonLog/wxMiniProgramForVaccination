package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.exception.TokenUnavailable;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class PayController {

    @Autowired
    private PlanService planService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private UserService userService;

    @Autowired
    private PayService payService;

    @Autowired
    private VaccineService vaccineService;

    @PostMapping("/user/pay/save")
    @PassToken(required = false)
    public Result save(@RequestBody Appoint appoint, @RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        appoint.setId(null);
        if (appoint.getPlanId() == null) {
            return Result.error("预约计划ID不能为空");
        }
        if (appoint.getAppointDate() == null) {
            return Result.error("预约日期不能为空");
        }
        if (appoint.getTimeSlot() == null) {
            return Result.error("预约时间段不能为空");
        }
        Plan plan = planService.getById(appoint.getPlanId());
        if (plan == null) {
            return Result.error("预约计划不存在");
        }
        Date now = new Date();
        if (now.after(plan.getEndDate())) {
            return Result.error("该预约计划已过期，请选择其他预约计划");
        }
        if (!(appoint.getAppointDate().after(now) && appoint.getAppointDate().before(new Date(plan.getEndDate().getTime() + 1000 * 60 * 60 * 24 * 1)))) {
            return Result.error("预约日期不在明日到预约计划结束之间");
        }
        if (plan.getAmount() < 1) {
            return Result.error("可预约量不足");
        }
        if (appoint.getTimeSlot() != 1 && appoint.getTimeSlot() != 0 && appoint.getTimeSlot() != 2) {
            return Result.error("预约时间段有误");
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
                return Result.error("该时间段无可预约疫苗");
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
                return Result.error("该时间段无可预约疫苗");
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
                return Result.error("该时间段无可预约疫苗");
            }
        }

        appointQueryWrapper.clear();;
        appointQueryWrapper.eq("status", 0);
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> list = appointService.list(appointQueryWrapper);
        if (list.size() != 0) {
            return Result.error("您还有未完成的预约");
        }
        User user = userService.getById(appoint.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        try {
            appointService.appoint(appoint, plan);
        } catch (Exception e) {
            return Result.error("预约失败");
        }
        return Result.ok("预约成功");
    }

    @GetMapping("/admin/pay/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String name) {
        Page<Pay> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());


        QueryWrapper<Pay> payQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                payQueryWrapper.eq("appoint_id", null);
            } else {
                QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
                List<Appoint> appointList = appointService.list(appointQueryWrapper);
                if (appointList.size() == 0) {
                    payQueryWrapper.eq("appoint_id", null);
                } else {
                    for (int i = 0; i < appointList.size(); i ++) {
                        payQueryWrapper.eq("appoint_id", appointList.get(i).getId());
                        payQueryWrapper.or();
                    }
                }
            }
        }

        page = payService.page(page, payQueryWrapper);
        List<Pay> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            result.put("cost", records.get(i).getCost());

            Appoint appoint = appointService.getById(records.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            Plan plan = planService.getById(appoint.getPlanId());
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }
}
