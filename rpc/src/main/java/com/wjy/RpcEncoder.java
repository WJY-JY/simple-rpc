package com.wjy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class RpcEncoder<I> extends MessageToByteEncoder<I> {

    private Class<I> clazz;

    private Serializer serializer;


    public RpcEncoder(Class<I> clazz, Serializer serializer) {
        super(clazz);
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, I msg, ByteBuf byteBuf) throws Exception {
        if (clazz != null && clazz.isInstance(msg)) {
            byte[] bytes = serializer.serialize(msg);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            // System.out.println("编码+" + msg);
        }
    }
}
