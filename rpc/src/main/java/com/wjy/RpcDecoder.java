package com.wjy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class RpcDecoder<I> extends ReplayingDecoder<RpcDecoder.LiveState> {

    // 解析状态
    public enum LiveState {LENGTH, CONTENT}

    private int length = 0;

    private Class<I> clazz;

    private Serializer serializer;

    public RpcDecoder(Class<I> clazz, Serializer serializer) {
        // 默认为验证长度状态
        super(LiveState.LENGTH);
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 根据状态选择流程
        switch (state()) {
            // 读取长度
            case LENGTH:
                length = byteBuf.readInt();
                // 这只读取content
                checkpoint(LiveState.CONTENT);
                break;
            // 读取内容
            case CONTENT:
                byte[] bytes = new byte[length];
                byteBuf.readBytes(bytes);
                if (bytes.length == 0) {
                    break;
                }
                final I deserialize = serializer.deserialize(clazz, bytes);
                list.add(deserialize);
                // 切换成读取长度
                checkpoint(LiveState.LENGTH);
                // System.out.println("解码+" + list.get(list.size() - 1));
                break;
            default:
                throw new IllegalStateException("invalid state:" + state());
        }
    }
}
