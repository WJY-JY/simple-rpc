package com.wjy;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * @author wjy
 * @date 2020/10/18
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }
}
