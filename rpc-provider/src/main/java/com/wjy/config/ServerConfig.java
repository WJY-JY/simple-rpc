package com.wjy.config;

import com.wjy.server.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wjy
 * @date 2020/10/18
 */
@Configuration
public class ServerConfig {

    /** 启动rpc服务 */
    @Bean
    public RpcServer rpcServer() throws Exception {
        final RpcServer rpcServer = new RpcServer();
        rpcServer.startRpcServer("127.0.0.1", 8999);
        return rpcServer;
    }
}
