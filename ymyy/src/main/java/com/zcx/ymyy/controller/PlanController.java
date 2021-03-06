package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private AppointService appointService;

    @GetMapping("/admin/plan/pageQuery")
    public Result pageQuery(PageQuery pageQuery, String name) {
        Page<Plan> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            QueryWrapper<Vaccine> vaccineQueryWrapper = new QueryWrapper<>();
            vaccineQueryWrapper.like("name", name);
            List<Vaccine> vaccineList = vaccineService.list(vaccineQueryWrapper);
            if (vaccineList.size() == 0) {
                planQueryWrapper.eq("vaccine_id", null);
            } else {
                for (int i = 0; i < vaccineList.size(); i ++) {
                    planQueryWrapper.eq("vaccine_id", vaccineList.get(i).getId());
                    planQueryWrapper.or();
                }
            }
        }

        page = planService.page(page, planQueryWrapper);
        List<Plan> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(records.get(i).getStartDate()));
            result.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(records.get(i).getEndDate()));
            result.put("startTimeMorning", records.get(i).getStartTimeMorning());
            result.put("endTimeMorning", records.get(i).getEndTimeMorning());
            result.put("startTimeAfternoon", records.get(i).getStartTimeAfternoon());
            result.put("endTimeAfternoon", records.get(i).getEndTimeAfternoon());
            result.put("startTimeEvening", records.get(i).getStartTimeEvening());
            result.put("endTimeEvening", records.get(i).getEndTimeEvening());

            Vaccine vaccine = vaccineService.getById(records.get(i).getVaccineId());
            result.put("vaccineName", vaccine.getName());

            InoculateSite inoculateSite = inoculateSiteService.getById(records.get(i).getInoculateSiteId());
            result.put("inoculateSiteName", inoculateSite.getName());
            result.put("amount", records.get(i).getAmount());
            result.put("morningLimit", records.get(i).getMorningLimit());
            result.put("afternoonLimit", records.get(i).getAfternoonLimit());
            result.put("eveningLimit", records.get(i).getEveningLimit());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PutMapping("/admin/plan/change")
    @PassToken(required = false)
    public Result change(Plan plan) {
        if (plan.getStartDate() == null) {
            return Result.error("????????????????????????");
        }
        if (plan.getEndDate() == null) {
            return Result.error("????????????????????????");
        }
        if (plan.getStartTimeMorning() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getMorningLimit() == null || plan.getMorningLimit() < 0) {
            return Result.error("???????????????????????????");
        }
        if (plan.getEndTimeMorning() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getStartTimeAfternoon() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getAfternoonLimit() == null || plan.getAfternoonLimit() < 0) {
            return Result.error("???????????????????????????");
        }
        if (plan.getEndTimeAfternoon() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getStartTimeEvening() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getEveningLimit() == null || plan.getEveningLimit() < 0) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getEndTimeEvening() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getAmount() == null) {
            return Result.error("???????????????????????????");
        }


        if (plan.getEndDate().before(plan.getStartDate())) {
            return Result.error("????????????");
        }
        if (plan.getStartTimeMorning() > plan.getEndTimeMorning()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeMorning() < 9 || plan.getStartTimeMorning() > 12 || plan.getEndTimeMorning() < 9 || plan.getEndTimeMorning() > 12) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeAfternoon() > plan.getEndTimeAfternoon()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeAfternoon() < 14 || plan.getStartTimeAfternoon() > 18 || plan.getEndTimeAfternoon() < 14 || plan.getEndTimeAfternoon() > 18) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeEvening() > plan.getEndTimeEvening()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeEvening() < 19 || plan.getStartTimeEvening() > 23 || plan.getEndTimeEvening() < 19 || plan.getEndTimeEvening() > 23) {
            return Result.error("????????????");
        }
        if (plan.getAmount() < 0) {
            return Result.error("????????????");
        }

        Plan target = planService.getById(plan.getId());
        if (target == null) {
            return Result.error("?????????????????????");
        }

        if (plan.getVaccineId() == null) {
            plan.setVaccineId(target.getVaccineId());
        } else {
            Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
            if (vaccine == null) {
                return Result.error("?????????????????????");
            }
        }
        if (plan.getInoculateSiteId() == null) {
            plan.setInoculateSiteId(target.getInoculateSiteId());
        } else {
            InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
            if (inoculateSite == null) {
                return Result.error("????????????????????????");
            }
        }
        planService.updateById(plan);
        return Result.ok("????????????");
    }

    @PostMapping("/admin/plan/save")
    @PassToken(required = false)
    public Result save(Plan plan) {
        plan.setId(null);
        if (plan.getStartDate() == null) {
            return Result.error("????????????????????????");
        }
        if (plan.getEndDate() == null) {
            return Result.error("????????????????????????");
        }
        if (plan.getStartTimeMorning() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getEndTimeMorning() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getStartTimeAfternoon() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getEndTimeAfternoon() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getStartTimeEvening() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getEndTimeEvening() == null) {
            return Result.error("??????????????????????????????");
        }
        if (plan.getAmount() == null) {
            return Result.error("???????????????????????????");
        }
        if (plan.getVaccineId() == null) {
            return Result.error("??????id????????????");
        }
        if (plan.getInoculateSiteId() == null) {
            return Result.error("?????????id????????????");
        }

        if (plan.getEndDate().before(plan.getStartDate())) {
            return Result.error("????????????");
        }

        if (plan.getStartTimeMorning() > plan.getEndTimeMorning()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeMorning() < 9 || plan.getStartTimeMorning() > 12 || plan.getEndTimeMorning() < 9 || plan.getEndTimeMorning() > 12) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeAfternoon() > plan.getEndTimeAfternoon()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeAfternoon() < 14 || plan.getStartTimeAfternoon() > 18 || plan.getEndTimeAfternoon() < 14 || plan.getEndTimeAfternoon() > 18) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeEvening() > plan.getEndTimeEvening()) {
            return Result.error("??????????????????");
        }
        if (plan.getStartTimeEvening() < 19 || plan.getStartTimeEvening() > 23 || plan.getEndTimeEvening() < 19 || plan.getEndTimeEvening() > 23) {
            return Result.error("??????????????????");
        }
        if (plan.getAmount() < 0) {
            return Result.error("????????????????????????");
        }


        Vaccine vaccine = vaccineService.getById(plan.getVaccineId());
        if (vaccine == null) {
            return Result.error("?????????????????????");
        }

        InoculateSite inoculateSite = inoculateSiteService.getById(plan.getInoculateSiteId());
        if (inoculateSite == null) {
            return Result.error("????????????????????????");
        }
        planService.save(plan);
        return Result.ok("????????????");
    }

    @DeleteMapping("/admin/plan/remove")
    @PassToken(required = false)
    public Result remove(Integer id) {
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("plan_id", id);
        if (appointService.count(appointQueryWrapper) > 1) {
            return Result.error("????????????,????????????");
        }
        planService.removeById(id);
        return Result.ok("????????????");
    }

    @GetMapping("/plan/findInoculateSites/{vaccineId}")
    public Result findInoculateSites(@PathVariable("vaccineId") Integer vaccineId) {
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.select("DISTINCT inoculate_site_id");
        planQueryWrapper.eq("vaccine_id", vaccineId);

        List<Plan> list = planService.list(planQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            InoculateSite inoculateSite = inoculateSiteService.getById(list.get(i).getInoculateSiteId());

            result.put("id", inoculateSite.getId());
            result.put("name", inoculateSite.getName());
            result.put("address", inoculateSite.getAddress());
            result.put("imgUrl", inoculateSite.getImgUrl());

            String provinceCode = inoculateSite.getProvinceCode();
            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", provinceCode);
            List<District> provinceList = districtService.list(districtQueryWrapper);
            result.put("province", provinceList.get(0).getName());

            String cityCode = inoculateSite.getCityCode();
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", cityCode);
            List<District> cityList = districtService.list(districtQueryWrapper);
            result.put("city", cityList.get(0).getName());

            String districtCode = inoculateSite.getDistrictCode();
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", districtCode);
            List<District> districtList = districtService.list(districtQueryWrapper);
            result.put("district", districtList.get(0).getName());
            results.add(result);
        }
        return Result.ok(results, "????????????");
    }

    @GetMapping("/plan/findPlans/{vaccineId}/{inoculateSiteId}")
    public Result findPlans(@PathVariable("vaccineId") Integer vaccineId, @PathVariable("inoculateSiteId") Integer inoculateSiteId) {
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq("vaccine_id", vaccineId);
        planQueryWrapper.eq("inoculate_site_id", inoculateSiteId);

        List<Plan> list = planService.list(planQueryWrapper);
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", list.get(i).getId());
            result.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(list.get(i).getStartDate()));
            result.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(list.get(i).getEndDate()));
            result.put("amount", list.get(i).getAmount());
            results.add(result);
        }
        return Result.ok(results, "????????????");
    }

    @PostMapping("/plan/findPlan")
    public Result findPlan(@RequestBody Appoint appoint) {
        if (appoint.getPlanId() == null) {
            return Result.error("????????????ID????????????");
        }
        Plan plan = planService.getById(appoint.getPlanId());
        if (plan == null) {
            return Result.error("?????????????????????");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.ne("status", 6);
        if (appoint.getAppointDate() != null) {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
        } else {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
        }
        appointQueryWrapper.eq("time_slot", 0);
        appointQueryWrapper.eq("plan_id", plan.getId());
        int morningAppointed = appointService.count(appointQueryWrapper);
        appointQueryWrapper.clear();
        appointQueryWrapper.ne("status", 6);
        if (appoint.getAppointDate() != null) {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
        } else {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
        }
        appointQueryWrapper.eq("time_slot", 1);
        appointQueryWrapper.eq("plan_id", plan.getId());
        int afternoonAppointed = appointService.count(appointQueryWrapper);
        appointQueryWrapper.clear();
        appointQueryWrapper.ne("status", 6);
        if (appoint.getAppointDate() != null) {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(appoint.getAppointDate()));
        } else {
            appointQueryWrapper.eq("appoint_date", new SimpleDateFormat("yyyy-MM-dd").format(plan.getEndDate()));
        }
        appointQueryWrapper.eq("time_slot", 2);
        appointQueryWrapper.eq("plan_id", plan.getId());
        int eveningAppointed = appointService.count(appointQueryWrapper);
        result.put("morningRemain", plan.getMorningLimit() - morningAppointed);
        result.put("afternoonRemain", plan.getAfternoonLimit() - afternoonAppointed);
        result.put("eveningRemain", plan.getEveningLimit() - eveningAppointed);
        return Result.ok(result, "????????????");
    }

    @GetMapping("/plan/findVaccine/{planId}")
    public Result findVaccine(@PathVariable("planId") Integer planId) {
        Plan plan = planService.getById(planId);
        if (plan == null) {
            return Result.error("?????????????????????");
        }
        return Result.ok(vaccineService.getById(plan.getVaccineId()), "????????????");
    }

}
