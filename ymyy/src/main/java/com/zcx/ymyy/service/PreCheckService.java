package com.zcx.ymyy.service;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.PreCheck;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PreCheckService extends IService<PreCheck> {

    void preCheck(Appoint appoint, PreCheck preCheck);

    void notSuited(Appoint appoint, PreCheck preCheck);

}
