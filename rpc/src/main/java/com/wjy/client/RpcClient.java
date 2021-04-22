package com.wjy.client;

import com.wjy.JSONSerializer;
import com.wjy.RpcEncoder;
import com.wjy.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class RpcClient {

    //1.创建一个线程池对象  -- 它要处理我们自定义事件
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //2.声明一个自定义事件处理器  UserClientHandler
    private ClientHandler clientHandler;

    private String ip;
    private int port = 8999;

    public RpcClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    //3.编写方法,初始化客户端  ( 创建连接池  bootStrap  设置bootstrap  连接服务器)
    public RpcClient initClient() throws InterruptedException {
        //1) 初始化UserClientHandler
        clientHandler = new ClientHandler();
        //2)创建连接池对象
        EventLoopGroup group = new NioEventLoopGroup();
        //3)创建客户端的引导对象
        Bootstrap bootstrap = new Bootstrap();
        //4)配置启动引导对象
        bootstrap.group(group)
                //设置通道为NIO
                .channel(NioSocketChannel.class)
                //设置请求协议为TCP
                .option(ChannelOption.TCP_NODELAY, true)
                //监听channel 并初始化
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //获取ChannelPipeline
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //设置编码
                        pipeline.addLast(new RpcEncoder<RpcRequest>(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        //添加自定义事件处理器
                        pipeline.addLast(clientHandler);
                    }
                });

        //5)连接服务端
        bootstrap.connect(ip, port).sync();

        System.out.println("==========>客户端启动成功 ip:" + ip + " port:" + port);

        return this;
    }

    //4.编写一个方法,使用JDK的动态代理创建对象
    // serviceClass 接口类型,根据哪个接口生成子类代理对象;   providerParam :  "UserService#sayHello#"
    // TODO 改成RPCRequest
    public Object createProxy(Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        //1)初始化客户端cliet
                        if (clientHandler == null) {
                            initClient();
                        }

                        //2)给UserClientHandler 设置param参数
                        final RpcRequest param = new RpcRequest();
                        param.setRequestId(UUID.randomUUID().toString());
                        param.setClassName(serviceClass.getName());
                        param.setMethodName(method.getName());
                        param.setParameterTypes(method.getParameterTypes());
                        param.setParameters(objects);
                        clientHandler.setParam(param);

                        //3).使用线程池,开启一个线程处理处理call() 写操作,并返回结果
                        Object result = executorService.submit(clientHandler).get();

                        //4)return 结果
                        return result;
                    }
                });
    }

}
