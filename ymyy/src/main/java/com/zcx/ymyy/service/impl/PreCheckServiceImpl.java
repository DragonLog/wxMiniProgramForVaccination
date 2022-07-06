package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.PreCheck;
import com.zcx.ymyy.mapper.CheckMapper;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.PreCheckService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PreCheckServiceImpl extends ServiceImpl<CheckMapper, PreCheck> implements PreCheckService {

    @Autowired
    private AppointService appointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preCheck(Appoint appoint, PreCheck preCheck) {  //预检

        if (appoint == null || preCheck == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(2);
        appointService.updateById(appoint);

        save(preCheck);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notSuited(Appoint appoint, PreCheck preCheck) { //不适宜接种
        if (appoint == null || preCheck == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(4);
        appointService.updateById(appoint);
        save(preCheck);
    }
}
