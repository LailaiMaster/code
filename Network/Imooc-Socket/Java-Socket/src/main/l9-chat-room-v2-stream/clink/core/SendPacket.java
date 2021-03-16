package clink.core;

import java.io.InputStream;

/**
 * 发送包的定义
 *
 * @param <Stream> 用于表示将要发送的数据，以流的形式读取并发送。
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 17:18
 */
public abstract class SendPacket<Stream extends InputStream> extends Packet<Stream> {

    private boolean isCanceled = false;

    public boolean isCanceled() {
        return isCanceled;
    }

}
