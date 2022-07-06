package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Vaccine;
import com.zcx.ymyy.mapper.VaccineMapper;
import com.zcx.ymyy.service.VaccineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class VaccineServiceImpl extends ServiceImpl<VaccineMapper, Vaccine> implements VaccineService {
}
