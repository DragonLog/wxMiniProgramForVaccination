package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcx.ymyy.entity.District;
import com.zcx.ymyy.entity.Result;
import com.zcx.ymyy.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/district")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    @GetMapping("/findAllProvinces")
    public Result findAllProvinces(){
        QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
        districtQueryWrapper.eq("parent", 86);
        List<District> list = districtService.list(districtQueryWrapper);
        return Result.ok(list, "查询成功");
    }

    @GetMapping("/findAllCities")
    public Result findAllCities(String code){
        QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
        districtQueryWrapper.eq("parent", code);
        List<District> list = districtService.list(districtQueryWrapper);
        return Result.ok(list, "查询成功");
    }

    @GetMapping("/findAllDistricts")
    public Result findAllDistricts(String code){
        QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
        districtQueryWrapper.eq("parent", code);
        List<District> list = districtService.list(districtQueryWrapper);
        return Result.ok(list, "查询成功");
    }

}
