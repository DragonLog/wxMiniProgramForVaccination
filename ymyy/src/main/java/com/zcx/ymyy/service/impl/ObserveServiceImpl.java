package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Observe;
import com.zcx.ymyy.mapper.ObserveMapper;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.ObserveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class ObserveServiceImpl extends ServiceImpl<ObserveMapper, Observe> implements ObserveService {

    @Autowired
    private AppointService appointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void observe(Appoint appoint, Observe observe) { //留观
        if (appoint == null || observe == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(4);
        appointService.updateById(appoint);

        observe.setIsFinish(1);
        observe.setEndTime(new Date());
        updateById(observe);
    }
}
