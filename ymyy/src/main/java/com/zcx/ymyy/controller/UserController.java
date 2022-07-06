package com.zcx.ymyy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.entity.Result;
import com.zcx.ymyy.exception.TokenUnavailable;
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
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private AppointService appointService;

    @Autowired
    private PlanService planService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private InoculateService inoculateService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private PayService payService;

    @Autowired
    private PreCheckService preCheckService;

    @DeleteMapping("/admin/user/remove")
    @PassToken(required = false)
    public Result remove(Integer id){
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", id);
        if (appointService.count(appointQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }
        userService.removeById(id);
        return Result.ok("删除成功");
    }


    @GetMapping("/admin/user/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String realName){
        Page<User> page = new Page<>();
        page.setCurrent(pageQuery.getCurrent());
        page.setSize(pageQuery.getLimit());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(realName)){
            userQueryWrapper.like("real_name", realName);
        }

        page = userService.page(page, userQueryWrapper);
        List<User> records = page.getRecords();

        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("username", records.get(i).getUsername());
            result.put("realName", records.get(i).getRealName());
            result.put("phone", records.get(i).getPhone());
            result.put("password", records.get(i).getPassword());
            result.put("cardId", records.get(i).getCardId());

            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", records.get(i).getDistrictCode());
            List<District> districtList = districtService.list(districtQueryWrapper);
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", districtList.get(0).getParent());
            List<District> cityList = districtService.list(districtQueryWrapper);
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", cityList.get(0).getParent());
            List<District> provinceList = districtService.list(districtQueryWrapper);
            result.put("district", provinceList.get(0).getName() + cityList.get(0).getName() + districtList.get(0).getName());
            result.put("address", records.get(i).getAddress());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PassToken(required = false)
    @PutMapping("/admin/user/change")
    public Result change(User user){
        if (StringUtils.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return Result.error("密码不能为空");
        }
        if (StringUtils.isBlank(user.getRealName())) {
            return Result.error("真实姓名不能为空");
        }
        if (StringUtils.isBlank(user.getCardId())) {
            return Result.error("身份证号不能为空");
        }
        if (StringUtils.isBlank(user.getPhone())) {
            return Result.error("手机号码不能为空");
        }
        if (StringUtils.isBlank(user.getAddress())) {
            return Result.error("详细地址不能为空");
        }

        User target = userService.getById(user.getId());
        if (target == null) {
            return Result.error("用户信息不存在");
        }

        if (StringUtils.isBlank(user.getDistrictCode())) {
            user.setDistrictCode(target.getDistrictCode());
        } else {
            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", user.getDistrictCode());
            List<District> districtList = districtService.list(districtQueryWrapper);
            if (districtList.size() == 0) {
                return Result.error("区域编码有误");
            } else {
                if (districtList.get(0).getParent().equals("86")) {
                    return Result.error("非区域级编码");
                }
                districtQueryWrapper.clear();
                districtQueryWrapper.eq("parent", districtList.get(0).getCode());
                if (districtService.list(districtQueryWrapper).size() != 0) {
                    return Result.error("非区域级编码");
                }
            }
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", user.getUsername());
        List<User> userList = userService.list(userQueryWrapper);

        if (userList.size() > 1) {
            Result.error("账号有误");
        }
        userService.updateById(user);
        return Result.ok("更新成功");
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody User user) {
        if (StringUtils.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return Result.error("用户密码不能为空");
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", user.getUsername());
        List<User> list = userService.list(userQueryWrapper);
        if (list.size() == 0) {
            return Result.error("用户不存在");
        }
        User target = list.get(0);
        if (!target.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }
        target.setPassword(null);

        QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
        districtQueryWrapper.eq("code", target.getDistrictCode());
        District district = districtService.list(districtQueryWrapper).get(0);
        districtQueryWrapper.clear();
        districtQueryWrapper.eq("code", district.getParent());
        District city = districtService.list(districtQueryWrapper).get(0);
        districtQueryWrapper.clear();
        districtQueryWrapper.eq("code", city.getParent());
        District province = districtService.list(districtQueryWrapper).get(0);

        JSONObject obj = new JSONObject();
        obj.put("area", province.getName() + city.getName() + district.getName());
        obj.put("user", target);
        obj.put("userType","user");
        obj.put("token", JwtUtils.createToken(target.getId() + "", target.getUsername(), target.getRealName(), "user"));
        return Result.ok(obj, "登录成功");
    }

    @PostMapping(value = "/validate")
    public Result validate(@RequestBody Map<String, Object> params) {
        if (params.get("token") == null || params.get("userType") == null) {
            return Result.error("参数有误");
        }
        String userType = null;
        try {
            userType = JwtUtils.getClaimByName((String) params.get("token"),"userType").asString();
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("验证失败");
        }
        if(!((String) params.get("userType")).equals(userType)){
            return Result.error("用户身份不匹配");
        }
        try {
            String userId = JwtUtils.getAudience((String) params.get("token"));
            JwtUtils.verifyToken((String) params.get("token"), userId);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("验证失败");
        }
        return Result.ok("验证成功");

    }

    @GetMapping("/user/findAppoint")
    @PassToken(required = false)
    public Result findAppoint(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        appointQueryWrapper.ne("status", 4);
        appointQueryWrapper.ne("status", 5);
        appointQueryWrapper.ne("status", 6);
        appointQueryWrapper.ne("status", 7);

        List<Appoint> list = appointService.list(appointQueryWrapper);
        if (list.size() > 1) {
            return Result.error("预约信息有误");
        }
        return Result.ok(list, "查询成功");
    }

    @PutMapping("/user/cancelAppoint")
    @PassToken(required = false)
    public Result cancelAppoint(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        appointQueryWrapper.eq("status", 0);
        List<Appoint> list = appointService.list(appointQueryWrapper);
        if (list.size() != 1) {
            return Result.error("取消预约失败，请刷新二维码");
        }
        Appoint appoint = list.get(0);
        Plan plan = planService.getById(appoint.getPlanId());
        if (plan == null) {
            return Result.error("预约信息有误");
        }
        appointService.cancelAppoint(appoint, plan);
        return Result.ok("更新成功");
    }

    @PostMapping("/user/save")
    @PassToken
    public Result save(@RequestBody User user) {
       user.setId(null);
       if (StringUtils.isBlank(user.getUsername())) {
           return Result.error("用户名不能为空");
       }
       if (StringUtils.isBlank(user.getPassword())) {
           return Result.error("密码不能为空");
       }
       if (StringUtils.isBlank(user.getRealName())) {
           return Result.error("姓名不能为空");
       }
       if (StringUtils.isBlank(user.getCardId())) {
           return Result.error("身份证号不能为空");
       }
       if (StringUtils.isBlank(user.getPhone())) {
           return Result.error("手机号不能为空");
       }
       if (StringUtils.isBlank(user.getAddress())) {
           return Result.error("地址不能为空");
       }
       if (user.getDistrictCode() == null) {
           return Result.error("区域编码不能为空");
       }

       QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
       userQueryWrapper.eq("username", user.getUsername());
       List<User> userList = userService.list(userQueryWrapper);
       if (userList.size() != 0) {
           return Result.error("用户名已存在");
       }

       QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
       districtQueryWrapper.eq("code", user.getDistrictCode());
       List<District> districtList = districtService.list(districtQueryWrapper);
       if (districtList.size() != 1) {
           return Result.error("区域编码有误");
       }

       userService.save(user);
       return Result.ok("新增成功");
    }

    @GetMapping("/user/findAppointHistories")
    @PassToken(required = false)
    public Result findAppointHistories(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> list = appointService.list(appointQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("appointDate", new SimpleDateFormat("yyyy-MM-dd").format(list.get(i).getAppointDate()));
            if (list.get(i).getStatus() == 0) {
                result.put("status", "待签到");
            } else if (list.get(i).getStatus() == 1) {
                result.put("status", "待预检");
            } else if (list.get(i).getStatus() == 2) {
                result.put("status", "待接种");
            } else if (list.get(i).getStatus() == 3) {
                result.put("status", "留观中");
            } else if (list.get(i).getStatus() == 4) {
                result.put("status", "接种结束");
            } else if (list.get(i).getStatus() == 5) {
                result.put("status", "预约过期");
            } else if (list.get(i).getStatus() == 6) {
                result.put("status", "取消预约");
            } else {
                result.put("status", "接种流程异常");
            }
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(list.get(i).getCreateTime()));
            Plan plan = planService.getById(list.get(i).getPlanId());
            if (list.get(i).getTimeSlot() == 0) {
                result.put("timeSlot", "上午" + plan.getStartTimeMorning() + "-" + plan.getEndTimeMorning() + "点");
            } else if (list.get(i).getTimeSlot() == 1) {
                result.put("timeSlot", "下午" + plan.getStartTimeAfternoon() + "-" + plan.getEndTimeAfternoon() + "点");
            } else {
                result.put("timeSlot", "夜晚" + plan.getStartTimeEvening() + "-" + plan.getEndTimeEvening() + "点");
            }
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/user/findInoculateHistories")
    @PassToken(required = false)
    public Result findInoculateHistories(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> appointList = appointService.list(appointQueryWrapper);


        QueryWrapper<Inoculate> inoculateQueryWrapper = new QueryWrapper<>();
        if (appointList.size() == 0) {
            inoculateQueryWrapper.eq("appoint_id", null);
        }
        for (int i = 0; i < appointList.size(); i ++) {
            inoculateQueryWrapper.eq("appoint_id", appointList.get(i).getId());
            inoculateQueryWrapper.or();
        }
        List<Inoculate> inoculateList = inoculateService.list(inoculateQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < inoculateList.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("part", inoculateList.get(i).getPart());
            result.put("vaccineBatchCode", inoculateList.get(i).getVaccineBatchCode());
            Worker worker = workerService.getById(inoculateList.get(i).getWorkerId());
            result.put("workerRealName", worker.getRealName());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(inoculateList.get(i).getCreateTime()));
            if (StringUtils.isBlank(inoculateList.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", inoculateList.get(i).getNote());
            }
            Appoint appoint = appointService.getById(inoculateList.get(i).getAppointId());
            Plan plan = planService.getById(appoint.getPlanId());
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/user/findPayHistories")
    @PassToken(required = false)
    public Result findPayHistories(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> appointList = appointService.list(appointQueryWrapper);

        QueryWrapper<Pay> payQueryWrapper = new QueryWrapper<>();
        if (appointList.size() == 0) {
            payQueryWrapper.eq("appoint_id", null);
        }
        for (int i = 0; i < appointList.size(); i ++) {
            payQueryWrapper.eq("appoint_id", appointList.get(i).getId());
            payQueryWrapper.or();
        }

        List<Pay> payList = payService.list(payQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < payList.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("cost", payList.get(i).getCost());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(payList.get(i).getCreateTime()));
            Appoint appoint = appointService.getById(payList.get(i).getAppointId());
            Plan plan = planService.getById(appoint.getPlanId());
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            result.put("vaccineName", vaccine.getName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @GetMapping("/user/findPreCheckHistories")
    @PassToken(required = false)
    public Result findPreCheckHistories(@RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("user_id", userId);
        List<Appoint> appointList = appointService.list(appointQueryWrapper);

        QueryWrapper<PreCheck> preCheckQueryWrapper = new QueryWrapper<>();
        if (appointList.size() == 0) {
            preCheckQueryWrapper.eq("appoint_id", null);
        }
        for (int i = 0; i < appointList.size(); i ++) {
            preCheckQueryWrapper.eq("appoint_id", appointList.get(i).getId());
            preCheckQueryWrapper.or();
        }

        List<PreCheck> preCheckList = preCheckService.list(preCheckQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < preCheckList.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            if (preCheckList.get(i).getIsMedicine() == 0) {
                result.put("medicine", "无");
            } else {
                result.put("medicine", preCheckList.get(i).getMedicine());
            }
            if (preCheckList.get(i).getIsContraindication() == 0) {
                result.put("contraindication", "无");
            } else {
                result.put("contraindication", preCheckList.get(i).getContraindication());
            }
            result.put("bloodPressureHigh", preCheckList.get(i).getBloodPressureHigh());
            result.put("bloodPressureLow", preCheckList.get(i).getBloodPressureLow());
            result.put("temperature", preCheckList.get(i).getTemperature());
            result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(preCheckList.get(i).getCreateTime()));
            if (StringUtils.isBlank(preCheckList.get(i).getNote())) {
                result.put("note", "无");
            } else {
                result.put("note", preCheckList.get(i).getNote());
            }
            Appoint appoint = appointService.getById(preCheckList.get(i).getAppointId());
            Worker worker = workerService.getById(preCheckList.get(i).getWorkerId());
            result.put("workRealName", worker.getRealName());
            Plan plan = planService.getById(appoint.getPlanId());
            InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            results.add(result);
        }
        return Result.ok(results, "查询成功");
    }

    @PutMapping("/user/change")
    @PassToken(required = false)
    public Result Change(@RequestBody User user, @RequestHeader("x-token") String token) {
        String userId = null;
        try {
            userId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        User target = userService.getById(userId);
        user.setId(target.getId());
        if (StringUtils.isBlank(user.getUsername())) {
            user.setUsername(target.getUsername());
        }
        if (!target.getUsername().equals(user.getUsername())) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username", user.getUsername());
            List<User> list = userService.list(userQueryWrapper);
            if (list.size() != 0) {
                return Result.error("用户名已存在");
            }
        }
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(target.getPassword());
        }
        user.setRealName(target.getRealName());
        user.setCardId(target.getCardId());
        user.setPhone(target.getPhone());
        if (StringUtils.isBlank(user.getAddress())) {
            user.setAddress(target.getAddress());
        }
        if (StringUtils.isBlank(user.getDistrictCode())) {
            user.setDistrictCode(target.getDistrictCode());
        } else {
            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", user.getDistrictCode());
            List<District> districtList = districtService.list(districtQueryWrapper);
            if (districtList.size() == 0) {
                return Result.error("区域编码有误");
            } else {
                if (districtList.get(0).getParent().equals("86")) {
                    return Result.error("非区域级编码");
                }
                districtQueryWrapper.clear();
                districtQueryWrapper.eq("parent", districtList.get(0).getCode());
                if (districtService.list(districtQueryWrapper).size() != 0) {
                    return Result.error("非区域级编码");
                }
            }
        }
        userService.updateById(user);
        return Result.ok("修改成功");
    }
}
