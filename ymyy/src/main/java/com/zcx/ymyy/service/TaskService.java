package com.zcx.ymyy.service;

import com.zcx.ymyy.entity.Appoint;

import java.util.List;

public interface TaskService {

    void change0To5(List<Appoint> appointList);

    void changeTo7(List<Appoint> appointList);
}
