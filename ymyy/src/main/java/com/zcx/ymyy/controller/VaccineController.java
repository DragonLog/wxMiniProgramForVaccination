package com.zcx.ymyy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.PageQuery;
import com.zcx.ymyy.entity.Plan;
import com.zcx.ymyy.entity.Vaccine;
import com.zcx.ymyy.service.PlanService;
import com.zcx.ymyy.service.VaccineService;
import com.zcx.ymyy.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@RestController
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private PlanService planService;

    @Value("${upload.image.vaccineImage.url}")
    private String uploadImageVaccineImageUrl;

    @PassToken(required = false)
    @GetMapping("/admin/vaccine/pageQuery")
    public Result pageQuery(PageQuery pageQuery, String name){
        Page<Vaccine> page = new Page<>();
        page.setSize(pageQuery.getLimit());
        page.setCurrent(pageQuery.getCurrent());

        QueryWrapper<Vaccine> vaccineQueryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(name)){
            vaccineQueryWrapper.like("name", name);
        }

        page = vaccineService.page(page, vaccineQueryWrapper);
        return Result.ok(page.getRecords(), (int) page.getTotal());
    }

    @PostMapping("/admin/vaccine/save")
    @PassToken(required = false)
    public Result save(Vaccine vaccine){
        vaccine.setId(null);
        if (StringUtils.isBlank(vaccine.getName())) {
            return Result.error("疫苗名称不能为空");
        }
        if (vaccine.getPrice() == null || StringUtils.isBlank(vaccine.getPrice() + "") || vaccine.getPrice() < 0) {
            return Result.error("疫苗价格不能为空");
        }
        if (StringUtils.isBlank(vaccine.getManufacturer())) {
            return Result.error("疫苗生产厂家不能为空");
        }
        if (StringUtils.isBlank(vaccine.getCategory())) {
            return Result.error("疫苗种类不能为空");
        }
        if (StringUtils.isBlank(vaccine.getDetail())) {
            return Result.error("疫苗细节不能为空");
        }
        if (StringUtils.isBlank(vaccine.getImgUrl())) {
            return Result.error("疫苗图片不能为空");
        }
        vaccine.setImgUrl("/" + vaccine.getImgUrl());
        vaccineService.save(vaccine);
        return Result.ok("新增成功");
    }

    @PutMapping("/admin/vaccine/change")
    @PassToken(required = false)
    public Result change(Vaccine vaccine){
        if (StringUtils.isBlank(vaccine.getName())) {
            return Result.error("疫苗名称不能为空");
        }
        if (vaccine.getPrice() == null || StringUtils.isBlank(vaccine.getPrice() + "") || vaccine.getPrice() < 0) {
            return Result.error("疫苗价格不能为空");
        }
        if (StringUtils.isBlank(vaccine.getManufacturer())) {
            return Result.error("疫苗生产厂家不能为空");
        }
        if (StringUtils.isBlank(vaccine.getCategory())) {
            return Result.error("疫苗种类不能为空");
        }
        if (StringUtils.isBlank(vaccine.getDetail())) {
            return Result.error("疫苗细节不能为空");
        }
        if (StringUtils.isBlank(vaccine.getImgUrl())) {
            return Result.error("疫苗图片不能为空");
        }
        Vaccine target = vaccineService.getById(vaccine.getId());
        if (target == null) {
            return Result.error("疫苗信息不存在");
        }
        vaccineService.updateById(vaccine);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/admin/vaccine/remove")
    public Result remove(Integer id){
        QueryWrapper<Plan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq("vaccine_id", id);
        if (planService.count(planQueryWrapper) > 0) {
            return Result.error("数据冗余，无法删除");
        }
        vaccineService.removeById(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/vaccine/findAll")
    public Result findAll() {
        List<Vaccine> list = vaccineService.list(null);
        return Result.ok(list, "查询成功");
    }

    @PostMapping("/admin/vaccineImage/upload")
    @PassToken(required = false)
    public Result uploadVaccineImage(@RequestParam("file") MultipartFile imgFile) {
        try {
            uploadImageVaccineImageUrl = new String(uploadImageVaccineImageUrl.getBytes("iso8859-1"), "UTF-8");
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
        File dest = new File(uploadImageVaccineImageUrl + "/" + fileName);
        try {
            imgFile.transferTo(dest);
        } catch (IOException e) {
            return Result.error("图片上传失败");
        }
        return Result.ok(fileName, "上传成功");
    }

}

