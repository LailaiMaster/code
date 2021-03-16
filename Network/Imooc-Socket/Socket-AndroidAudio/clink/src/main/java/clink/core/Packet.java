package clink.core;

import java.io.Closeable;
import java.io.IOException;

/**
 * 公共的数据封装，提供了类型以及数据长度的定义。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 16:35
 */
public abstract class Packet<Stream extends Closeable> implements Closeable {

    /**
     * 最大包大小，5个字节满载组成的 Long 类型。
     */
    public static final long MAX_PACKET_SIZE = (((0xFFL) << 32) | ((0xFFL) << 24) | ((0xFFL) << 16) | ((0xFFL) << 8) | ((0xFFL)));

    /**
     * BYTES 类型
     */
    public static final byte TYPE_MEMORY_BYTES = 1;

    /**
     * String 类型
     */
    public static final byte TYPE_MEMORY_STRING = 2;

    /**
     * 文件 类型
     */
    public static final byte TYPE_STREAM_FILE = 3;

    /**
     * 长链接流 类型
     */
    public static final byte TYPE_STREAM_DIRECT = 4;

    /**
     * 包的长度
     */
    protected long length;

    private Stream stream;

    /**
     * 包的长度
     *
     * @return 长度
     */
    public long getLength() {
        return length;
    }

    /**
     * 打开包实体数据对于的流，如果已经打开了，就直接返回。
     */
    public final Stream open() {
        if (stream == null) {
            stream = createStream();
        }
        return stream;
    }

    /**
     * 类型，直接通过方法得到:
     * <ul>
     * <li>{@link #TYPE_MEMORY_BYTES}</li>
     * <li>{@link #TYPE_MEMORY_STRING}</li>
     * <li>{@link #TYPE_STREAM_FILE}</li>
     * <li>{@link #TYPE_STREAM_DIRECT}</li>
     * </ul>
     *
     * @return 类型
     */
    public abstract byte getType();

    /**
     * 对外的关闭资源操作，如果流处于打开状态应当进行关闭
     *
     * @throws IOException IO异常
     */
    @Override
    public final void close() throws IOException {
        if (stream != null) {
            closeStream(stream);
            stream = null;
        }
    }

    /**
     * 创建一个对应实体数据的流。
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

    /**
     * 头部额外信息，用于携带额外的校验信息等
     *
     * @return byte 数组，最大 255 长度。
     */
    public byte[] headerInfo() {
        return null;
    }

}