package clink.core;

import java.io.Closeable;
import java.io.IOException;

/**
 * 公共的数据封装，提供了类型以及数据长度的定义。
 *
 * @param <Stream> 基于 Stream 的 Packet，不同的 Packet 对应不同 Stream，Stream 可以认为是数据的载体，比如 {@link java.io.InputStream} 或者 {@link java.io.OutputStream}。
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 16:35
 */
public abstract class Packet<Stream extends Closeable> implements Closeable {

    /**
     * 包的长度
     */
    protected long mLength;

    // BYTES 类型
    public static final byte TYPE_MEMORY_BYTES = 1;
    // String 类型
    public static final byte TYPE_MEMORY_STRING = 2;
    // 文件 类型
    public static final byte TYPE_STREAM_FILE = 3;
    // 长链接流 类型
    public static final byte TYPE_STREAM_DIRECT = 4;

    /**
     * 数据载体
     */
    private Stream mStream;

    /**
     * 类型，直接通过方法得到:
     * <p>
     * {@link #TYPE_MEMORY_BYTES}
     * {@link #TYPE_MEMORY_STRING}
     * {@link #TYPE_STREAM_FILE}
     * {@link #TYPE_STREAM_DIRECT}
     *
     * @return 类型
     */
    public abstract byte getType();

    /**
     * 包的长度
     *
     * @return 长度
     */
    public long getLength() {
        return mLength;
    }

    /**
     * 对外的关闭资源操作，如果流处于打开状态应当进行关闭
     *
     * @throws IOException IO异常
     */
    @Override
    public final void close() throws IOException {
        if (mStream != null) {
            closeStream(mStream);
            mStream = null;
        }
    }

    /**
     * 打开流
     */
    public final Stream open() {
        if (mStream == null) {
            mStream = createStream();
        }
        return mStream;
    }

    /**
     * 创建一个流
     *
     * @return {@link java.io.InputStream} or {@link java.io.OutputStream}
     */
    protected abstract Stream createStream();

    /**
     * 关闭流，当前方法会调用流的关闭操作。
     *
     * @param stream 待关闭的流
     * @throws IOException IO异常
     */
    protected void closeStream(Stream stream) throws IOException {
        stream.close();
    }

}