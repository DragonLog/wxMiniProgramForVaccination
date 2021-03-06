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
public class InoculateController {

    @Autowired
    private InoculateService inoculateService;

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private PlanService planService;

    @GetMapping("/admin/inoculate/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<Inoculate> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Inoculate> inoculateQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                inoculateQueryWrapper.eq("appoint_id", null);
            } else {
                QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
                List<Appoint> appointList = appointService.list(appointQueryWrapper);
                if (appointList.size() == 0) {
                    inoculateQueryWrapper.eq("appoint_id", null);
                } else {
                    for (int i = 0; i < appointList.size(); i ++) {
                        inoculateQueryWrapper.eq("appoint_id", appointList.get(i).getId());
                        inoculateQueryWrapper.or();
                    }
                }
            }
        }

        page = inoculateService.page(page, inoculateQueryWrapper);
        List<Inoculate> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i <records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("part", records.get(i).getPart());
            result.put("vaccineBatchCode", records.get(i).getVaccineBatchCode());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            Worker worker = workerService.getById(records.get(i).getWorkerId());
            result.put("workerRealName", worker.getRealName());
            Appoint appoint = appointService.getById(records.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            Plan plan = planService.getById(appoint.getPlanId());
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            if (StringUtils.isBlank(records.get(i).getNote())) {
                result.put("note", "???");
            } else {
                result.put("note", records.get(i).getNote());
            }
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }


    @PostMapping("/worker/inoculate/ok")
    @PassToken(required = false)
    public Result ok(@RequestBody Appoint appoint, @RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token??????");
        }

        if (appoint.getId() == null) {
            return Result.error("??????ID??????");
        }

        Appoint target = appointService.getById(appoint.getId());
        if (target == null) {
            return Result.error("??????ID?????????");
        }
        if (target.getStatus() != 2) {
            return Result.error("????????????");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(target.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("??????????????????");
        }

        return Result.ok("????????????");
    }

    @PostMapping("/worker/inoculate/save")
    @PassToken(required = false)
    public Result save(@RequestBody Inoculate inoculate, @RequestHeader("x-token") String token, Integer isSucceeded) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token??????");
        }

        if (isSucceeded == null) {
            return Result.error("??????????????????????????????");
        }
        if (isSucceeded != 1 && isSucceeded != 0) {
            return Result.error("?????????????????????????????????01");
        }

        inoculate.setId(null);
        if (StringUtils.isBlank(inoculate.getPart())) {
            return Result.error("????????????????????????");
        }
        if (!("??????".equals(inoculate.getPart()) || "??????".equals(inoculate.getPart()) || "??????".equals(inoculate.getPart()) || "??????".equals(inoculate.getPart()) || "??????".equals(inoculate.getPart()))) {
            return Result.error("??????????????????");
        }
        if (StringUtils.isBlank(inoculate.getVaccineBatchCode())) {
            return Result.error("????????????????????????");
        }
        if (StringUtils.isBlank(inoculate.getNote())) {
            return Result.error("??????????????????");
        }

        if (inoculate.getAppointId() == null) {
            return Result.error("??????ID??????");
        }

        Appoint appoint = appointService.getById(inoculate.getAppointId());
        if (appoint == null) {
            return Result.error("??????ID?????????");
        }
        if (appoint.getStatus() != 2) {
            return Result.error("????????????");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(appoint.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("??????????????????");
        }

        inoculate.setWorkerId(Integer.parseInt(workerId));
        inoculate.setCreateTime(new Date());

        if (isSucceeded == 0) {
            inoculateService.inoculate(appoint, inoculate);
            return Result.ok("????????????");
        }
        inoculateService.notSucceeded(appoint, inoculate);
        return Result.ok("????????????????????????");
    }
}

