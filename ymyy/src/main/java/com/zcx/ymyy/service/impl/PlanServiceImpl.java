package com.zcx.ymyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.Plan;
import com.zcx.ymyy.mapper.PlanMapper;
import com.zcx.ymyy.service.PlanService;
import org.springframework.stereotype.Service;


@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
}
