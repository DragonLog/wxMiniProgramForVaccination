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
public class SignController {

    @Autowired
    private SignService signService;

    @Autowired
    private UserService userService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private PlanService planService;

    @PassToken(required = false)
    @GetMapping("/admin/sign/pageQuery")
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<Sign> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                signQueryWrapper.eq("appoint_id", null);
            } else {
                QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
                List<Appoint> appointList = appointService.list(appointQueryWrapper);
                if (appointList.size() == 0) {
                    signQueryWrapper.eq("appoint_id", null);
                } else {
                    for (int i = 0; i < appointList.size(); i ++) {
                        signQueryWrapper.eq("appoint_id", appointList.get(i).getId());
                        signQueryWrapper.or();
                    }
                }
            }
        }

        page = signService.page(page, signQueryWrapper);
        List<Sign> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            Worker worker = workerService.getById(records.get(i).getWorkerId());
            result.put("workerRealName", worker.getRealName());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            Appoint appoint = appointService.getById(records.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PostMapping("/worker/sign/save")
    @PassToken(required = false)
    public Result save(@RequestBody Appoint appoint, @RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        if (appoint.getId() == null) {
            return Result.error("预约ID为空");
        }

        Appoint target = appointService.getById(appoint.getId());
        if (target == null) {
            return Result.error("预约ID不存在");
        }
        if (target.getStatus() != 0) {
            return Result.error("预约二维码无效");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDateStr = simpleDateFormat.format(new Date());
        String appointDateStr = simpleDateFormat.format(target.getAppointDate());
        if (!appointDateStr.equals(nowDateStr)) {
            return Result.error("预约时间不匹配");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(target.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("预约接种点不匹配");
        }

        int timeSlot = target.getTimeSlot();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeSlot == 0) {
            if (hour < plan.getStartTimeMorning() || hour > plan.getEndTimeMorning()) {
                return Result.error("预约时间段不匹配");
            }
        } else if (timeSlot == 1) {
            if (hour < plan.getStartTimeAfternoon() || hour > plan.getEndTimeAfternoon()) {
                return Result.error("预约时间段不匹配");
            }
        } else {
            if (hour < plan.getStartTimeEvening() || hour > plan.getEndTimeEvening()) {
                return Result.error("预约时间段不匹配");
            }
        }

        Sign sign = new Sign();
        sign.setAppointId(target.getId());
        sign.setCreateTime(new Date());
        sign.setWorkerId(worker.getId());

        signService.sign(appoint, sign);

        return Result.error("签到成功");
    }
}

