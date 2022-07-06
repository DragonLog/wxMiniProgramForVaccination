package com.zcx.ymyy.service;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Inoculate;
import com.baomidou.mybatisplus.extension.service.IService;

public interface InoculateService extends IService<Inoculate> {

    void inoculate(Appoint appoint, Inoculate inoculate);

    void notSucceeded(Appoint appoint, Inoculate inoculate);

}
