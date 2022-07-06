package com.zcx.ymyy.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.entity.User;
import com.zcx.ymyy.mapper.UserMapper;
import com.zcx.ymyy.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
