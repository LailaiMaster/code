package clink.core;

import java.io.Closeable;
import java.io.IOException;

/**
 * 一个数据接收者
 */
public interface Receiver extends Closeable {

    /**
     * 表示希望进行数据接收操作，当可以进行数据接收时，会通过 {@link #setReceiveListener(IoArgs.IoArgsEventProcessor)} 方法设置的回调进行通知。
     *
     * @return true 成功
     * @throws IOException 可能不可写
     */
    boolean postReceiveAsync() throws IOException;

    /**
     * 设置一个IO事件提供者，postReceiveAsync 方法调用后，通过此回调提供 IoArgs 和处理 IO 操作结果。
     */
    void setReceiveListener(IoArgs.IoArgsEventProcessor ioArgsEventProcessor);

}

