package clink.core;

import java.io.Closeable;

/**
 * 公共的数据封装，提供了类型以及数据长度的定义。
 *
 * <pre>
 *     Packet 实现了 Closeable 接口，因为在发送的过程中要对包进行拆分，为了不破坏原始包的数据，需要做数据备份，在拆包的过程中必然存在中间状态，close 用于恢复包的初始状态。
 * </pre>
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 16:35
 */
public abstract class Packet implements Closeable {

    /**
     * 包的长度
     */
    protected int length;

    /**
     * 包所代表的数据类型
     */
    protected byte type;

    public byte getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

}
