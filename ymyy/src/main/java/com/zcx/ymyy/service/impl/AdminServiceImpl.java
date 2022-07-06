package com.zcx.ymyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.Admin;
import com.zcx.ymyy.mapper.AdminMapper;
import com.zcx.ymyy.service.AdminService;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
