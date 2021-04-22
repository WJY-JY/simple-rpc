package com.wjy.server;

import com.wjy.JSONSerializer;
import com.wjy.RpcDecoder;
import com.wjy.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class RpcServer implements ApplicationContextAware {

    private ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    public void startRpcServer(String ip, int port) throws Exception {

        //1.创建两个线程池对象
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        //2.创建服务端的启动引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //3.配置启动引导对象
        serverBootstrap.group(bossGroup, workGroup)
                //设置通道为NIO
                .channel(NioServerSocketChannel.class)
                //创建监听channel
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        //获取管道对象
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //给管道对象pipeLine 设置编码
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new RpcDecoder<>(RpcRequest.class, new JSONSerializer()));
                        //把我们自己定义的ChannelHandler添加到通道中
                        pipeline.addLast(new ServerHandler(applicationContext));
                    }
                });

        //4.绑定端口
        serverBootstrap.bind(port).sync();

        System.out.println("==========>服务端启动成功 ip:" + ip + " port:" + port);
    }
}
