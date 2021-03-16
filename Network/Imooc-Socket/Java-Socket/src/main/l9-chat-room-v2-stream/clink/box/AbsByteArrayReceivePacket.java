package clink.box;

import java.io.ByteArrayOutputStream;

import clink.core.ReceivePacket;

/**
 * 定义最基础的基于{@link ByteArrayOutputStream}的输出接收包
 *
 * @param <Entity> 对应的实体范性，需定义{@link ByteArrayOutputStream}流最终转化为什么数据实体
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/24 0:04
 */
public abstract class AbsByteArrayReceivePacket<Entity> extends ReceivePacket<ByteArrayOutputStream, Entity> {

    public AbsByteArrayReceivePacket(long len) {
        super(len);
    }

    @Override
    protected ByteArrayOutputStream createStream() {
        /*
            1. 根据包的长度，创建对应的容量的 ByteArrayOutputStream。
            2. 这里 long 转 int，可以认为内存数据远远小于int。
         TODO：如果要发送的数据很长，会造成内存压力。
        */
        return new ByteArrayOutputStream((int) getLength());
    }

}
