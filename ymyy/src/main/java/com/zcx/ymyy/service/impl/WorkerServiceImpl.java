package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Worker;
import com.zcx.ymyy.mapper.WorkerMapper;
import com.zcx.ymyy.service.WorkerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class WorkerServiceImpl extends ServiceImpl<WorkerMapper, Worker> implements WorkerService {
}
