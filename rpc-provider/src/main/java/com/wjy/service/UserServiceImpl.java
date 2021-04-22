package com.wjy.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Override
    public String sayHello(String msg) {
        System.out.println("接收客户端数据：" + msg);
        return "success 当前毫秒：" + System.currentTimeMillis();
    }
}
