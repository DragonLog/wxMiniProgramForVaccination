package com.zcx.ymyy.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.ymyy.entity.PageQuery;
import com.zcx.ymyy.entity.Result;
import com.zcx.ymyy.annotation.PassToken;
import com.zcx.ymyy.entity.Admin;
import com.zcx.ymyy.exception.TokenUnavailable;
import com.zcx.ymyy.service.AdminService;
import com.zcx.ymyy.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PassToken(required = false)
    @GetMapping("/pageQuery")
    public Result pageQueryAdmin(PageQuery pageQuery, String realName) {
        Page<Admin> page = new Page<>();
        page.setCurrent(pageQuery.getCurrent());
        page.setSize(pageQuery.getLimit());

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(realName)) {
            queryWrapper.like("real_name", realName);
        }
        page = adminService.page(page, queryWrapper);
        List<Admin> records = page.getRecords();
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < records.size(); i ++) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", records.get(i).getId());
            result.put("username", records.get(i).getUsername());
            result.put("realName", records.get(i).getRealName());
            results.add(result);
        }
        return Result.ok(results, (int) page.getTotal());
    }


    @PostMapping("/login")
    public Result login(@RequestBody Admin admin){
        if (StringUtils.isBlank(admin.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(admin.getPassword())){
            return Result.error("密码不能为空");
        }

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", admin.getUsername());

        List<Admin> list = adminService.list(queryWrapper);
        if (list.size() == 0) {
            return Result.error("用户信息不存在");
        }
        Admin target = list.get(0);
        if(!target.getPassword().equals(admin.getPassword())){
            return Result.error("密码错误");
        }
        target.setPassword(null);
        JSONObject obj = new JSONObject();
        obj.put("admin", target);
        obj.put("userType", "admin");
        obj.put("token", JwtUtils.createToken(target.getId() + "", target.getUsername(), target.getRealName(),"admin"));
        return Result.ok(obj, "登录成功");
    }

    @PutMapping("/change")
    @PassToken(required = false)
    public Result change(@RequestBody Admin admin, @RequestHeader("x-token") String token) {
        String adminId = null;
        try {
            adminId = JwtUtils.getAudience(token);
        } catch (TokenUnavailable tokenUnavailable) {
            return Result.error("token无效");
        }

        Admin target = adminService.getById(adminId);
        if (StringUtils.isBlank(admin.getPassword())) {
            admin.setPassword(target.getPassword());
        }
        if (StringUtils.isBlank(admin.getUsername())) {
            admin.setUsername(target.getUsername());
        }
        admin.setId(target.getId());
        if (!target.getUsername().equals(admin.getUsername())) {
            QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
            adminQueryWrapper.eq("username", admin.getUsername());
            List<Admin> list = adminService.list(adminQueryWrapper);
            if (list.size() != 0) {
                return Result.error("用户名已存在");
            }
        }
        admin.setRealName(target.getRealName());
        adminService.updateById(admin);
        return Result.ok("修改成功");
    }

}
