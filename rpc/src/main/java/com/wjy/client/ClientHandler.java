package com.wjy.client;

import com.wjy.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class ClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    //1.定义成员变量
    private ChannelHandlerContext context; //事件处理器上下文对象 (存储handler信息,写操作)
    private Object result; // 记录服务器返回的数据
    private RpcRequest param; //记录将要返送给服务器的数据

    //2.实现channelActive  客户端和服务器连接时,该方法就自动执行
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化ChannelHandlerContext
        this.context = ctx;
    }


    //3.实现channelRead 当我们读到服务器数据,该方法自动执行
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // System.out.println("客户端接收：" + msg);
        //将读到的服务器的数据msg ,设置为成员变量的值
        result = msg;
        notify();
    }

    //4.将客户端的数写到服务器
    @Override
    public synchronized Object call() throws Exception {
        // System.out.println("客户端发起请求！");
        //context给服务器写数据
        context.writeAndFlush(param);
        wait();
        // System.out.println("客户端请求返回值：" + result);
        return result;
    }

    //5.设置参数的方法
    public void setParam(RpcRequest param) {
        this.param = param;
    }
}