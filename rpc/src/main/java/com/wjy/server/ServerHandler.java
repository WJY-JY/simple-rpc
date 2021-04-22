package com.wjy.server;

import com.wjy.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ApplicationContext applicationContext;

    public ServerHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // 开始读取
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcRequest) {
            final String className = ((RpcRequest) msg).getClassName();
            final String methodName = ((RpcRequest) msg).getMethodName();
            final Object[] parameters = ((RpcRequest) msg).getParameters();
            final Class<?>[] parameterTypes = ((RpcRequest) msg).getParameterTypes();

            // 获取目标类
            final Class<?> serviceClass = Class.forName(className);
            // 从容器获取类实例
            Object obj = applicationContext.getBean(serviceClass);
            // 获取方法
            final Method method = serviceClass.getMethod(methodName, parameterTypes);
            // 调用方法
            final Object result = method.invoke(obj, parameters);

            ctx.writeAndFlush(result);
            // System.out.println("服务器输出：" + result);
        }
    }

    // 读取完全
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    // 出现异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
