package com.zcx.ymyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.District;
import com.zcx.ymyy.mapper.DistrictMapper;
import com.zcx.ymyy.service.DistrictService;
import org.springframework.stereotype.Service;


@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District> implements DistrictService {
}
