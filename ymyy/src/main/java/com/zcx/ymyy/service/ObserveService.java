package com.zcx.ymyy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Observe;

public interface ObserveService extends IService<Observe> {

    void observe(Appoint appoint, Observe observe);

}
