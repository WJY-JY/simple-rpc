package com.wjy.boot;

import com.wjy.service.IUserService;
import com.wjy.client.RpcClient;

import java.util.Date;

public class ClientApp {


    public static void main(String[] args) throws InterruptedException {

        final RpcClient rpcClient = new RpcClient("127.0.0.1", 8999).initClient();

        final IUserService userService = (IUserService) rpcClient.createProxy(IUserService.class);

        while (true) {
            final String rst = userService.sayHello("这里是客户端发起请求" + new Date());
            System.out.println("客户端接收请求：" + rst);
            Thread.sleep(2000);
        }
    }
}
