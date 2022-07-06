package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.service.*;
import com.zcx.ymyy.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/eChart")
public class EChartsController {

    @Autowired
    private AppointService appointService;

    @Autowired
    private SignService signService;

    @Autowired
    private PreCheckService preCheckService;

    @Autowired
    private InoculateService inoculateService;

    @Autowired
    private ObserveService observeService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private PlanService planService;

    @GetMapping("/bar")
    public Result getBarPage1(){
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.ne("status", 6);
        int appointCount = appointService.count(appointQueryWrapper);
        int signCount = signService.count();
        int preCheckCount = preCheckService.count();
        int inoculateCount = inoculateService.count();
        int observeCount = observeService.count();
        List<Integer> list = new ArrayList<>();
        list.add(appointCount);
        list.add(signCount);
        list.add(preCheckCount);
        list.add(inoculateCount);
        list.add(observeCount);
        return Result.ok(list, "查询成功");
    }


    @GetMapping("/line")
    public Result getLinePage(){
        Map<String, List> results = new HashMap<>();
        List<String> dayListOfMonth = null;
        try {
            dayListOfMonth = getNDaysList(null, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 15, "yyyy-MM-dd");
        } catch (ParseException e) {
            return Result.error("时间处理有误");
        }

        List<Integer> appointList = new ArrayList<>();
        List<Integer> signList = new ArrayList<>();
        List<Integer> preCheckList = new ArrayList<>();
        List<Integer> inoculateList = new ArrayList<>();
        List<Integer> observerList = new ArrayList<>();

        for (int i = 0; i < dayListOfMonth.size(); i ++){
            String date = dayListOfMonth.get(i);

            QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
            appointQueryWrapper.between("create_time", date + " 00:00:00",date + " 23.59.59");
            appointQueryWrapper.ne("status", 6);
            appointList.add(appointService.count(appointQueryWrapper));

            QueryWrapper<Sign> signQueryWrapper = new QueryWrapper<>();
            signQueryWrapper.between("create_time", date + " 00:00:00",date + " 23.59.59");
            signList.add(signService.count(signQueryWrapper));

            QueryWrapper<PreCheck> preCheckQueryWrapper = new QueryWrapper<>();
            preCheckQueryWrapper.between("create_time", date + " 00:00:00",date + " 23.59.59");
            preCheckList.add(preCheckService.count(preCheckQueryWrapper));

            QueryWrapper<Inoculate> inoculateQueryWrapper = new QueryWrapper<>();
            inoculateQueryWrapper.between("create_time", date + " 00:00:00",date + " 23.59.59");
            inoculateList.add(inoculateService.count(inoculateQueryWrapper));

            QueryWrapper<Observe> observeQueryWrapper = new QueryWrapper<>();
            observeQueryWrapper.between("create_time", date + " 00:00:00",date + " 23.59.59");
            observerList.add(observeService.count(observeQueryWrapper));
        }

        try {
            dayListOfMonth = getNDaysList(null, new SimpleDateFormat("MM-dd").format(new Date()), 15, "MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.error("处理时间失败");
        }
        results.put("dateList", dayListOfMonth);
        results.put("appointList",appointList);
        results.put("signList",signList);
        results.put("preCheckList",preCheckList);
        results.put("inoculateList",inoculateList);
        results.put("observeList",observerList);

        return Result.ok(results, "查询成功");
    }

    /**
     *  用户可以传入startTime或endTime任意一个或两个，也可以不传入
     *  当传入的时间间隔太长时，默认返回最近的nday
     */
    public static List<String> getNDaysList(String startTime, String endTime, int nday, String pattern) throws ParseException {
        int ndaycurrent = nday - 1;
        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat(pattern);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -ndaycurrent);
        Date start = calendar.getTime();
        Date end = new Date();

        if (StringUtils.isNotBlank(startTime) && StringUtils.isBlank(endTime)){
            //如果用户只选择了startTime, endTime为null
            start = dateFormat.parse(startTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(start);
            calendar1.add(Calendar.DATE, ndaycurrent);
            end = calendar1.getTime();
        } else if(StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)){
            //如果用户只选择了endTime,startTime为null
            end = dateFormat.parse(endTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(end);
            calendar2.add(Calendar.DATE, -ndaycurrent);
            start = calendar2.getTime();
        }else if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
            //如果用户选择了startTime和endTime
            Date start1 = dateFormat.parse(startTime);
            Date end1 = dateFormat.parse(endTime);
            int a = (int) ((end1.getTime() - start1.getTime()) / (1000*3600*24));
            if (a <= ndaycurrent) {
                //如果小于等于n天
                start = dateFormat.parse(startTime);
                end = dateFormat.parse(endTime);
            }
        }

        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        tempEnd.add(Calendar.DATE, +1);

        while (tempStart.before(tempEnd)) {
            days.add(dateFormat.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return days;
    }

    @GetMapping("/test")
    public Result test() {
        List<Vaccine> vaccineList = vaccineService.list();
        Map<String, Object> result = new HashMap<>();
        List<String> xList = new ArrayList<>();
        List<Integer> yList = new ArrayList<>();
        if (vaccineList.size() < 3) {
            result.put("x", xList);
            result.put("y", yList);
            return Result.ok(result, "疫苗数量小于3");
        }
        Map<Vaccine, Integer> map = new HashMap<>();
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        for (int i = 0; i < vaccineList.size(); i ++) {
            planQueryWrapper.clear();
            planQueryWrapper.eq("vaccine_id", vaccineList.get(i).getId());
            List<Plan> planList = planService.list(planQueryWrapper);
            Set<Integer> planIdList = new HashSet<>();
            if (planList.size() == 0) {
                planIdList.add(null);
            }
            for (int j = 0; j < planList.size(); j ++) {
                planIdList.add(planList.get(j).getId());
            }
            appointQueryWrapper.clear();
            appointQueryWrapper.ne("status", 6);
            appointQueryWrapper.in("plan_id", planIdList);
            map.put(vaccineList.get(i), appointService.count(appointQueryWrapper));
        }
        List<Map.Entry<Vaccine,Integer>> sortList = new ArrayList(map.entrySet());
        Collections.sort(sortList, new Comparator<Map.Entry<Vaccine, Integer>>() {
            @Override
            public int compare(Map.Entry<Vaccine, Integer> o1, Map.Entry<Vaccine, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        for (int i = 2; i >= 0; i --) {
            xList.add(sortList.get(i).getKey().getName());
        }
        for (int i = 2; i >= 0; i --) {
            yList.add(sortList.get(i).getValue());
        }
        result.put("x", xList);
        result.put("y", yList);
        return Result.ok(result, "测试成功");
    }
}
