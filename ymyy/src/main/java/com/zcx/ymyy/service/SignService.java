package com.zcx.ymyy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Sign;

public interface SignService extends IService<Sign> {

    void sign(Appoint appoint, Sign sign);

}
