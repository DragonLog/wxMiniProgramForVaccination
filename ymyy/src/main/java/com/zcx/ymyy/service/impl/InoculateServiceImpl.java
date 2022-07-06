package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Inoculate;
import com.zcx.ymyy.entity.Observe;
import com.zcx.ymyy.mapper.*;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.InoculateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.service.ObserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class InoculateServiceImpl extends ServiceImpl<InoculateMapper, Inoculate> implements InoculateService {

    @Autowired
    private AppointService appointService;

    @Autowired
    private ObserveService observeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inoculate(Appoint appoint, Inoculate inoculate) {   //接种
        if (appoint == null || inoculate == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(3);
        appointService.updateById(appoint);

        save(inoculate);

        Observe observe = new Observe();
        observe.setAppointId(appoint.getId());
        observe.setCreateTime(new Date());
        observe.setWorkerId(inoculate.getWorkerId());
        observe.setIsFinish(0);
        observe.setEndTime(new Date(observe.getCreateTime().getTime() + 1000 * 60 * 30));
        observeService.save(observe);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notSucceeded(Appoint appoint, Inoculate inoculate) {    //接种不成功
        if (appoint == null || inoculate == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(4);
        appointService.updateById(appoint);

        save(inoculate);
    }
}
