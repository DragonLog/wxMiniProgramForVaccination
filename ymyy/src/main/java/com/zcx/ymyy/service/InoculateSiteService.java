package com.zcx.ymyy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.ymyy.entity.InoculateSite;

import java.util.List;
import java.util.Map;

public interface InoculateSiteService extends IService<InoculateSite> {

    List<Map<String, Object>> findAll();

}
