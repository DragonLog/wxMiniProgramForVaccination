package com.zcx.ymyy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.exception.TokenUnavailable;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private  InoculateService inoculateService;

    @Autowired
    private ObserveService observeService;

    @Autowired
    private PreCheckService preCheckService;

    @Autowired
    private SignService signService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private PlanService planService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("/worker/findSignHistories")
    @PassToken(required = false)
    public Result findSignHistories(@RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
        signQueryWrapper.eq("worker_id", workerId);
        List<Sign> list = signService.list(signQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            Appoint appoint = appointService.getById(list.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getCreateTime()));
            result.put("userRealName", user.getRealName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/worker/findPreCheckHistories")
    @PassToken(required = false)
    public Result findPreCheckHistories(@RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<PreCheck> preCheckQueryWrapper = new QueryWrapper<>();
        preCheckQueryWrapper.eq("worker_id", workerId);
        List<PreCheck> list = preCheckService.list(preCheckQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            if (list.get(i).getIsMedicine() == 0) {
                result.put("medicine", "无");
            } else {
                result.put("medicine", list.get(i).getMedicine());
            }
            if (list.get(i).getIsContraindication() == 0) {
                result.put("contraindication", "无");
            } else {
                result.put("contraindication", list.get(i).getContraindication());
            }
            result.put("bloodPressureHigh", list.get(i).getBloodPressureHigh());
            result.put("bloodPressureLow", list.get(i).getBloodPressureLow());
            result.put("temperature", list.get(i).getTemperature());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getCreateTime()));
            if (StringUtils.isBlank(list.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", list.get(i).getNote());
            }
            Appoint appoint = appointService.getById(list.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/worker/findInoculateHistories")
    @PassToken(required = false)
    public Result findInoculateHistories(@RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Inoculate> inoculateQueryWrapper = new QueryWrapper<>();
        inoculateQueryWrapper.eq("worker_id", workerId);
        List<Inoculate> list = inoculateService.list(inoculateQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("part", list.get(i).getPart());
            result.put("vaccineBatchCode", list.get(i).getVaccineBatchCode());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getCreateTime()));
            if (StringUtils.isBlank(list.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", list.get(i).getNote());
            }
            Appoint appoint = appointService.getById(list.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            Plan plan = planService.getById(appoint.getPlanId());
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/worker/findObserveHistories")
    @PassToken(required = false)
    public Result findObserveHistories(@RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
        observeQueryWrapper.eq("worker_id", workerId);
        List<Observe> list = observeService.list(observeQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getCreateTime()));
            if (list.get(i).getIsFinish() == 0) {
                result.put("status", "留观中");
                result.put("endTime", "留观尚未结束");
            } else {
                result.put("status", "留观结束");
                result.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getEndTime()));
            }
            if (StringUtils.isBlank(list.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", list.get(i).getNote());
            }
            Appoint appoint = appointService.getById(list.get(i).getAppointId());
            User user = userService.getById(appoint.getUserId());
            result.put("userRealName", user.getRealName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/admin/worker/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String realName){
        Page<Worker> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(realName)){
            workerQueryWrapper.like("real_name", realName);
        }

        page = workerService.page(page, workerQueryWrapper);
        List<Worker> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("username", records.get(i).getUsername());
            result.put("realName", records.get(i).getRealName());
            result.put("phone", records.get(i).getPhone());
            result.put("cardId", records.get(i).getCardId());
            result.put("password", records.get(i).getPassword());
            InoculateSite inoculateSite = inoculateSiteService.getById(records.get(i).getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PassToken(required = false)
    @PutMapping("/admin/worker/change")
    public Result change(Worker worker){
       if (StringUtils.isBlank(worker.getUsername())) {
           return Result.error("用户名不能为空");
       }
       if (StringUtils.isBlank(worker.getRealName())) {
           return Result.error("真实姓名不能为空");
       }
       if (StringUtils.isBlank(worker.getCardId())) {
           return Result.error("身份证号不能为空");
       }
       if (StringUtils.isBlank(worker.getPhone())) {
           return Result.error("手机号不能为空");
       }
       if (StringUtils.isBlank(worker.getPassword())) {
           return Result.error("密码不能为空");
       }

       Worker target = workerService.getById(worker.getId());
       if (target == null) {
           return Result.error("用户信息不存在");
       }
       if (worker.getInoculateSiteId() == null) {
           worker.setInoculateSiteId(target.getInoculateSiteId());
       } else {
           InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
           if (inoculateSite == null) {
               return Result.error("用户信息有误");
           }
       }

       QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
       workerQueryWrapper.eq("username", worker.getUsername());
       List<Worker> list = workerService.list(workerQueryWrapper);
       if (list.size() > 1) {
           return Result.error("用户信息有误");
       }
       workerService.updateById(worker);
       return Result.ok("更新成功");
    }

    @PostMapping("/admin/worker/save")
    @PassToken(required = false)
    public Result save(Worker worker){
        worker.setId(null);
        if (StringUtils.isBlank(worker.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(worker.getPassword())) {
            return Result.error("密码不能为空");
        }
        if (StringUtils.isBlank(worker.getRealName())) {
            return Result.error("真实姓名不能为空");
        }
        if (StringUtils.isBlank(worker.getCardId())) {
            return Result.error("身份证号不能为空");
        }
        if (StringUtils.isBlank(worker.getPhone())) {
            return Result.error("电话号码不能为空");
        }
        if (worker.getInoculateSiteId() == null) {
            return Result.error("接种点不能为空");
        }

        QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
        workerQueryWrapper.eq("username", worker.getUsername());
        List<Worker> workerList = workerService.list(workerQueryWrapper);
        if (workerList.size() != 0) {
            return Result.error("用户名已存在");
        }

        InoculateSite inoculateSite = inoculateSiteService.getById(worker.getInoculateSiteId());
        if (inoculateSite == null) {
            return Result.error("接种点不存在");
        }
        workerService.save(worker);
        return Result.ok("新增成功");
    }

    @DeleteMapping("/admin/worker/remove")
    @PassToken(required = false)
    public Result remove(Integer id){
        QueryWrapper<Inoculate> inoculateQueryWrapper = new QueryWrapper<>();
        inoculateQueryWrapper.eq("worker_id", id);
        if (inoculateService.count(inoculateQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }

        QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
        observeQueryWrapper.eq("worker_id", id);
        if (observeService.count(observeQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }

        QueryWrapper<PreCheck> preCheckQueryWrapper = new QueryWrapper<>();
        preCheckQueryWrapper.eq("worker_id", id);
        if (preCheckService.count(preCheckQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }

        QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
        signQueryWrapper.eq("worker_id", id);
        if (signService.count(signQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }
       workerService.removeById(id);
       return Result.ok("删除成功");
    }

    @PostMapping("/worker/login")
    public Result login(@RequestBody Worker worker) {
        if (StringUtils.isBlank(worker.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(worker.getPassword())) {
            return Result.error("密码不能为空");
        }

        QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
        workerQueryWrapper.eq("username", worker.getUsername());
        List<Worker> list = workerService.list(workerQueryWrapper);
        if (list.size() == 0) {
            return Result.error("用户不存在");
        }
        Worker target = list.get(0);
        if (!target.getPassword().equals(worker.getPassword())) {
            return Result.error("密码错误");
        }
        target.setPassword(null);

        InoculateSite inoculateSite = inoculateSiteService.getById(target.getInoculateSiteId());


        JSONObject obj = new JSONObject();
        obj.put("inoculateSiteName", inoculateSite.getName());
        obj.put("worker", target);
        obj.put("userType","worker");
        obj.put("token", JwtUtils.createToken(target.getId() + "", target.getUsername(), target.getRealName(), "worker"));
        return Result.ok(obj, "登录成功");
    }

    @PutMapping("/worker/change")
    @PassToken(required = false)
    public Result Change(@RequestBody Worker worker, @RequestHeader("x-token") String token) {
        String workerId = null;
        try {
            workerId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        Worker target = workerService.getById(workerId);
        worker.setId(target.getId());
        if (StringUtils.isBlank(worker.getUsername())) {
            worker.setUsername(target.getUsername());
        }
        if (!target.getUsername().equals(worker.getUsername())) {
            QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
            workerQueryWrapper.eq("username", worker.getUsername());
            List<Worker> list = workerService.list(workerQueryWrapper);
            if (list.size() != 0) {
                return Result.error("用户名已存在");
            }
        }
        if (StringUtils.isBlank(worker.getPassword())) {
            worker.setPassword(target.getPassword());
        }
        worker.setRealName(target.getRealName());
        worker.setCardId(target.getCardId());
        worker.setPhone(target.getPhone());
        worker.setInoculateSiteId(target.getInoculateSiteId());
        workerService.updateById(worker);
        return Result.ok("修改成功");
    }
}

