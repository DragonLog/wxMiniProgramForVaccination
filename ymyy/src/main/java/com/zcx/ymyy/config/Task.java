package com.zcx.ymyy.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


@Configuration
@EnableScheduling
public class Task {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    @Autowired
    private AppointService appointService;

    @Autowired
    private TaskService taskService;

    @Scheduled(cron = "0 0 1 * * *")
    private void Task() {
        logger.info("开始处理预约时间已经过期的预约");
        QueryWrapper<Appoint> appointQueryWrapper = new QueryWrapper<>();
        appointQueryWrapper.eq("status", 0);
        List<Appoint> appointList1 = appointService.list(appointQueryWrapper);
        taskService.change0To5(appointList1);
        logger.info("处理预约时间已经过期的预约结束");

        logger.info("开始处理接种流程未正常结束的预约");
        appointQueryWrapper.clear();
        appointQueryWrapper.eq("status", 1);
        appointQueryWrapper.or();
        appointQueryWrapper.eq("status", 2);
        appointQueryWrapper.or();
        appointQueryWrapper.eq("status", 3);
        List<Appoint> appointList2 = appointService.list(appointQueryWrapper);
        taskService.changeTo7(appointList2);
        logger.info("处理接种流程未正常结束的预约结束");
    }

}
