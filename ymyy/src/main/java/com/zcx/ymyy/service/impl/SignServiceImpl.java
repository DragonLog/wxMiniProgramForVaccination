package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Sign;
import com.zcx.ymyy.mapper.SignMapper;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.SignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign> implements SignService {

    @Autowired
    private AppointService appointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sign(Appoint appoint, Sign sign) {  //签到
        if (appoint == null || sign == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(1);
        appointService.updateById(appoint);

        save(sign);
    }
}
