package com.zcx.ymyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.InoculateSite;
import com.zcx.ymyy.mapper.InoculateSiteMapper;
import com.zcx.ymyy.service.InoculateSiteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InoculateSiteServiceImpl extends ServiceImpl<InoculateSiteMapper, InoculateSite> implements InoculateSiteService {

    @Override
    public List<Map<String, Object>> findAll() {
        return getBaseMapper().selectAll();
    }


}
