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
public class ObserveController {

    @Autowired
    private ObserveService observeService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private PlanService planService;

    @GetMapping("/admin/observe/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<Observe> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                observeQueryWrapper.eq("appoint_id", null);
            } else {
                QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
                List<Appoint> appointList = appointService.list(appointQueryWrapper);
                if (appointList.size() == 0) {
                    observeQueryWrapper.eq("appoint_id", null);
                } else {
                    for (int i = 0; i < appointList.size(); i ++) {
                        observeQueryWrapper.eq("appoint_id", appointList.get(i).getId());
                        observeQueryWrapper.or();
                    }
                }
            }
        }

        page = observeService.page(page, observeQueryWrapper);
        List<Observe> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            result.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getEndTime()));
            Worker worker = workerService.getById(records.get(i).getWorkerId());
            result.put("workerRealName", worker.getRealName());
            Appoint appoint = appointService.getById(records.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            if (records.get(i).getIsFinish() == 0) {
                result.put("isFinish", "留观中");
            } else {
                result.put("isFinish", "留观结束");
            }
            if (StringUtils.isBlank(records.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", records.get(i).getNote());
            }
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PostMapping("/worker/observe/ok")
    @PassToken(required = false)
    public Result ok(@RequestBody Appoint appoint, @RequestHeader("x-token") String token) {
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
        if (target.getStatus() != 3) {
            return Result.error("请先接种");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(target.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("接种点不匹配");
        }

        QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
        observeQueryWrapper.eq("appoint_id", target.getId());
        observeQueryWrapper.eq("is_finish", 0);
        List<Observe> list = observeService.list(observeQueryWrapper);

        return Result.ok(list.get(0), "留观中");
    }

    @PutMapping("/worker/observe/change")
    @PassToken(required = false)
    public Result change(@RequestBody Observe observe, @RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        if (StringUtils.isBlank(observe.getNote())) {
            return Result.error("备注不能为空");
        }

        if (observe.getAppointId() == null) {
            return Result.error("预约ID为空");
        }

        Appoint appoint = appointService.getById(observe.getAppointId());
        if (appoint == null) {
            return Result.error("预约ID不存在");
        }
        if (appoint.getStatus() != 3) {
            return Result.error("请先接种");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(appoint.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("接种点不匹配");
        }

        QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
        observeQueryWrapper.eq("appoint_id", appoint.getId());
        observeQueryWrapper.eq("is_finish", 0);
        List<Observe> list = observeService.list(observeQueryWrapper);
        list.get(0).setNote(observe.getNote());

        observe.setWorkerId(Integer.parseInt(workerId));
        observeService.observe(appoint, list.get(0));
        return Result.ok("留观成功，全部流程结束");
    }
}

