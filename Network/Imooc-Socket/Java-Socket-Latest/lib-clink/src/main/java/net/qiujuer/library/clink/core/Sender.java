package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.io.IOException;

public interface Sender extends Closeable {
    void setSendListener(IoArgs.IoArgsEventProcessor processor);

    /**
     * 失败则抛出异常，成功不抛出
     *
     * @throws Exception 异常信息
     */
    void postSendAsync() throws Exception;

    /**
     * 获取输出数据的时间
     *
     * @return 毫秒
     */
    long getLastWriteTime();
}
