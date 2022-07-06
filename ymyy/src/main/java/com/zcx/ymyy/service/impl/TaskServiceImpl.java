package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.service.AppointService;
import com.zcx.ymyy.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private AppointService appointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void change0To5(List<Appoint> appointList) {
        for (int i = 0; i < appointList.size(); i ++) {
            if (new Date().after(appointList.get(i).getAppointDate())) {
                appointList.get(i).setStatus(5);
                appointService.updateById(appointList.get(i));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeTo7(List<Appoint> appointList) {
        for (int i = 0; i < appointList.size(); i ++) {
            appointList.get(i).setStatus(7);
            appointService.updateById(appointList.get(i));
        }
    }
}
