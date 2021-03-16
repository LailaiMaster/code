package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.io.IOException;

public interface Receiver extends Closeable {
    void setReceiveListener(IoArgs.IoArgsEventProcessor processor);

    /**
     * 注册失败则抛出异常
     *
     * @throws Exception 异常信息
     */
    void postReceiveAsync() throws Exception;

    /**
     * 获取读取数据的时间
     *
     * @return 毫秒
     */
    long getLastReadTime();
}
