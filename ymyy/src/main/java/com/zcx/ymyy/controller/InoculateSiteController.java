package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.entity.*;
import com.zcx.ymyy.service.DistrictService;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.service.InoculateSiteService;
import com.zcx.ymyy.service.PlanService;
import com.zcx.ymyy.service.WorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
public class InoculateSiteController {

    @Autowired
    private InoculateSiteService inoculateSiteService;

    @Value("${upload.image.inoculateSiteImage.url}")
    private String uploadImageInoculateSiteImageUrl;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private PlanService planService;

    @Autowired
    private WorkerService workerService;

    @GetMapping("/admin/inoculateSite/pageQuery")
    @PassToken(required = false)
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<InoculateSite> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<InoculateSite> inoculateSiteQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            inoculateSiteQueryWrapper.like("name", name);
        }

        page = inoculateSiteService.page(page, inoculateSiteQueryWrapper);
        List<InoculateSite> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++){
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("name", records.get(i).getName());
            result.put("address", records.get(i).getAddress());
            result.put("imgUrl", records.get(i).getImgUrl());

            String provinceCode = records.get(i).getProvinceCode();
            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", provinceCode);
            List<District> provinceList = districtService.list(districtQueryWrapper);
            result.put("province", provinceList.get(0).getName());

            String cityCode = records.get(i).getCityCode();
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", cityCode);
            List<District> cityList = districtService.list(districtQueryWrapper);
            result.put("city", cityList.get(0).getName());

            String districtCode = records.get(i).getDistrictCode();
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", districtCode);
            List<District> districtList = districtService.list(districtQueryWrapper);
            result.put("district", districtList.get(0).getName());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }

    @PostMapping("/admin/inoculateSite/save")
    @PassToken(required = false)
    public Result save(InoculateSite inoculateSite){
        inoculateSite.setId(null);
        if (StringUtils.isBlank(inoculateSite.getName())) {
            return Result.error("接种点名称不能为空");
        }
        if (StringUtils.isBlank(inoculateSite.getAddress())) {
            return Result.error("接种点地址不能为空");
        }
        if (StringUtils.isBlank(inoculateSite.getDistrictCode())) {
            return Result.error("区域编码不能为空");
        }
        if (StringUtils.isBlank(inoculateSite.getImgUrl())) {
            return Result.error("图片不能为空");
        }
        inoculateSite.setImgUrl("/" + inoculateSite.getImgUrl());

        QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
        districtQueryWrapper.eq("code", inoculateSite.getDistrictCode());
        List<District> districtList = districtService.list(districtQueryWrapper);
        if (districtList.size() != 1) {
            return Result.error("区域数据有误");
        }
        districtQueryWrapper.clear();
        districtQueryWrapper.eq("code", districtList.get(0).getParent());
        List<District> cityList = districtService.list(districtQueryWrapper);
        if (cityList.size() != 1) {
            return Result.error("区域数据有误");
        }
        inoculateSite.setCityCode(cityList.get(0).getCode());
        districtQueryWrapper.clear();
        districtQueryWrapper.eq("code", cityList.get(0).getParent());
        List<District> provinceList = districtService.list(districtQueryWrapper);
        if (provinceList.size() != 1) {
            return Result.error("区域数据有误");
        }
        inoculateSite.setProvinceCode(provinceList.get(0).getCode());
        inoculateSiteService.save(inoculateSite);
        return Result.ok("新增成功");
    }

    @PutMapping("/admin/inoculateSite/change")
    @PassToken(required = false)
    public Result change(InoculateSite inoculateSite){
        if (StringUtils.isBlank(inoculateSite.getName())) {
            return Result.error("接种点名称不能为空");
        }
        if (StringUtils.isBlank(inoculateSite.getAddress())) {
            return Result.error("接种点地址不能为空");
        }
        if (StringUtils.isBlank(inoculateSite.getImgUrl())) {
            return Result.error("图片不能为空");
        }

        InoculateSite target = inoculateSiteService.getById(inoculateSite.getId());
        if (target == null) {
            return Result.error("接种点信息不存在");
        }
        inoculateSite.setImgUrl("/" + inoculateSite.getImgUrl());
        if (StringUtils.isBlank(inoculateSite.getDistrictCode())) {
            inoculateSite.setDistrictCode(target.getDistrictCode());
            inoculateSite.setCityCode(target.getCityCode());
            inoculateSite.setProvinceCode(target.getProvinceCode());
        } else {
            QueryWrapper<District> districtQueryWrapper = new QueryWrapper<>();
            districtQueryWrapper.eq("code", inoculateSite.getDistrictCode());
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

            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", districtList.get(0).getParent());
            List<District> cityList = districtService.list(districtQueryWrapper);
            inoculateSite.setCityCode(cityList.get(0).getCode());
            districtQueryWrapper.clear();
            districtQueryWrapper.eq("code", cityList.get(0).getParent());
            List<District> provinceList = districtService.list(districtQueryWrapper);
            inoculateSite.setProvinceCode(provinceList.get(0).getCode());
        }
        inoculateSiteService.updateById(inoculateSite);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/admin/inoculate/remove")
    @PassToken(required = false)
    public Result remove(Integer id){
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq("inoculate_site_id", id);
        if (planService.count(planQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }

        QueryWrapper<Worker> workerQueryWrapper = new QueryWrapper<>();
        workerQueryWrapper.eq("inoculate_site_id", id);
        if (workerService.count(workerQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }
        inoculateSiteService.removeById(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/inoculateSite/findAllInoculateSites")
    public Result findAllInoculateSites(String code) {
        QueryWrapper<InoculateSite> inoculateSiteQueryWrapper = new QueryWrapper<>();
        inoculateSiteQueryWrapper.eq("district_code", code);
        List<InoculateSite> list = inoculateSiteService.list(inoculateSiteQueryWrapper);
        return Result.ok(list, "查询成功");
    }

    @PostMapping("/admin/inoculateSiteImage/upload")
    @PassToken(required = false)
    public Result uploadInoculateSiteImage(@RequestParam("file") MultipartFile imgFile) {
        try {
            uploadImageInoculateSiteImageUrl = new String(uploadImageInoculateSiteImageUrl.getBytes("iso8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
           throw new RuntimeException("字符转码异常！");
        }

        if (imgFile == null) {
            return Result.error("图片为空");
        }
        String fileName = imgFile.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return Result.error("文件名不能为空");
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return Result.error("文件后缀不能为空");
        }
        String suffix = fileName.substring(index);
        if (StringUtils.isBlank(suffix) || (!".png".equals(suffix) && !".jpg".equals(suffix) && !".jpeg".equals(suffix))) {
            return Result.error("文件格式不正确");
        }
        fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(uploadImageInoculateSiteImageUrl + "/" + fileName);
        try {
            imgFile.transferTo(dest);
        } catch (IOException e) {
            return Result.error("图片上传失败");
        }
        return Result.ok(fileName, "上传成功");
    }

}

