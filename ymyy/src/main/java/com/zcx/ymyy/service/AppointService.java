package com.zcx.ymyy.service;

import com.zcx.ymyy.entity.Appoint;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.ymyy.entity.Plan;

public interface AppointService extends IService<Appoint> {

    void appoint(Appoint appoint, Plan plan) throws Exception;

    void cancelAppoint(Appoint appoint, Plan plan);
}
