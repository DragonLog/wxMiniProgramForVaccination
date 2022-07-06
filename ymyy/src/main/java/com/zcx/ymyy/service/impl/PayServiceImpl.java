package com.zcx.ymyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.Pay;
import com.zcx.ymyy.mapper.PayMapper;
import com.zcx.ymyy.service.PayService;
import org.springframework.stereotype.Service;


@Service
public class PayServiceImpl extends ServiceImpl<PayMapper, Pay> implements PayService {
}
