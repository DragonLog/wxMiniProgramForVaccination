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
public class PreCheckController {

    @Autowired
    private PreCheckService preCheckService;

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

    @PassToken(required = false)
    @GetMapping("/admin/preCheck/pageQuery")
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<PreCheck> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<PreCheck> preCheckQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("real_name", name);
            List<User> userList = userService.list(userQueryWrapper);
            if (userList.size() == 0) {
                preCheckQueryWrapper.eq("appoint_id", null);
            } else {
                QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
                for (int i = 0; i < userList.size(); i ++) {
                    appointQueryWrapper.eq("user_id", userList.get(i).getId());
                    appointQueryWrapper.or();
                }
                List<Appoint> appointList = appointService.list(appointQueryWrapper);
                if (appointList.size() == 0) {
                    preCheckQueryWrapper.eq("appoint_id", null);
                } else {
                    for (int i = 0; i < appointList.size(); i ++) {
                        preCheckQueryWrapper.eq("appoint_id", appointList.get(i).getId());
                        preCheckQueryWrapper.or();
                    }
                }
            }
        }

        page = preCheckService.page(page, preCheckQueryWrapper);
        List<PreCheck> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("isMedicine", records.get(i).getIsMedicine() == 1 ? "???" : "???");
            if (records.get(i).getIsMedicine() != 1) {
                result.put("medicine", "???");
            } else {
                result.put("medicine", records.get(i).getMedicine());
            }
            result.put("isContraindication", records.get(i).getIsMedicine() == 1 ? "???" : "???");
            if (records.get(i).getIsContraindication() != 1) {
                result.put("contraindication", "???");
            } else {
                result.put("contraindication", records.get(i).getContraindication());
            }
            if (StringUtils.isBlank(records.get(i).getNote())) {
                result.put("note", "???");
            } else {
                result.put("note", records.get(i).getNote());
            }
            result.put("bloodPressureHigh", records.get(i).getBloodPressureHigh());
            result.put("bloodPressureLow", records.get(i).getBloodPressureLow());
            result.put("temperature", records.get(i).getTemperature());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(records.get(i).getCreateTime()));
            Worker worker = workerService.getById(records.get(i).getWorkerId());
            result.put("workerRealName", worker.getRealName());
            Appoint appoint = appointService.getById(records.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
       return Result.ok(results, (int) page.getTotal());
    }

    @PostMapping("/worker/preCheck/ok")
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
        if (target.getStatus() != 1) {
            return Result.error("????????????");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(target.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("?????????????????????");
        }

        return Result.ok("????????????");
    }

    @PostMapping("/worker/preCheck/save")
    @PassToken(required = false)
    public Result save(@RequestBody PreCheck preCheck, @RequestHeader("x-token") String token, Integer isSuited) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token??????");
        }

        if (isSuited == null) {
            return Result.error("??????????????????????????????");
        }
        if (isSuited != 1 && isSuited != 0) {
            return Result.error("?????????????????????????????????01");
        }

        preCheck.setId(null);
        if (StringUtils.isBlank(preCheck.getNote())) {
            return Result.error("??????????????????");
        }
        if (preCheck.getIsMedicine() == null) {
            return Result.error("????????????????????????");
        }
        if (preCheck.getIsContraindication() == null) {
            return Result.error("???????????????????????????");
        }
        if (preCheck.getIsMedicine() != 0 && preCheck.getIsMedicine() != 1) {
            return Result.error("???????????????????????????01");
        }
        if (preCheck.getIsContraindication() != 0 && preCheck.getIsContraindication() != 1) {
            return Result.error("??????????????????????????????01");
        }
        if (preCheck.getIsMedicine() == 0) {
            preCheck.setMedicine(null);
        } else {
            if (StringUtils.isBlank(preCheck.getMedicine())) {
                return Result.error("????????????????????????");
            }
        }
        if (preCheck.getIsContraindication() == 0) {
            preCheck.setContraindication(null);
        } else {
            if (StringUtils.isBlank(preCheck.getContraindication())) {
                return Result.error("???????????????????????????");
            }
        }
        if (preCheck.getBloodPressureHigh() == null || preCheck.getBloodPressureLow() == null) {
            return Result.error("?????????????????????");
        }
        if (preCheck.getBloodPressureLow() < 0 || preCheck.getBloodPressureLow() > 300 || preCheck.getBloodPressureHigh() < 0 || preCheck.getBloodPressureHigh() > 300) {
            return Result.error("???????????????");
        }
        if (preCheck.getTemperature() == null) {
            return Result.error("??????????????????");
        }
        if (preCheck.getTemperature() < 0 || preCheck.getTemperature() > 50) {
            return Result.error("???????????????");
        }

        if (preCheck.getAppointId() == null) {
            return Result.error("??????ID??????");
        }
        Appoint appoint = appointService.getById(preCheck.getAppointId());
        if (appoint == null) {
            return Result.error("??????ID?????????");
        }
        if (appoint.getStatus() != 1) {
            return Result.error("????????????");
        }

        Worker worker = workerService.getById(workerId);
        Plan plan = planService.getById(appoint.getPlanId());
        if (worker.getInoculateSiteId().intValue() != plan.getInoculateSiteId().intValue()) {
            return Result.error("??????????????????");
        }

        preCheck.setWorkerId(Integer.parseInt(workerId));
        preCheck.setCreateTime(new Date());

        if (isSuited == 0) {
            preCheckService.preCheck(appoint, preCheck);
            return Result.ok("????????????");
        }
        preCheckService.notSuited(appoint, preCheck);
        return Result.ok("???????????????????????????");
    }
}

