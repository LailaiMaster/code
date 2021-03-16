package net.qiujuer.library.clink.core;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

@SuppressWarnings("Duplicates")
public class IoArgs {
    // 单次操作最大区间
    private volatile int limit;
    // 是否需要消费所有的区间（读取、写入）
    private final boolean isNeedConsumeRemaining;
    // Buffer
    private final ByteBuffer buffer;

    public IoArgs() {
        this(256);
    }

    public IoArgs(int size) {
        this(size, true);
    }

    public IoArgs(int size, boolean isNeedConsumeRemaining) {
        this.limit = size;
        this.isNeedConsumeRemaining = isNeedConsumeRemaining;
        this.buffer = ByteBuffer.allocate(size);
    }

    /**
     * 从bytes数组进行消费
     */
    public int readFrom(byte[] bytes, int offset, int count) {
        int size = Math.min(count, buffer.remaining());
        if (size <= 0) {
            return 0;
        }
        buffer.put(bytes, offset, size);
        return size;
    }

    /**
     * 写入数据到bytes中
     */
    public int writeTo(byte[] bytes, int offset) {
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.get(bytes, offset, size);
        return size;
    }

    /**
     * 从bytes中读取数据
     */
    public int readFrom(ReadableByteChannel channel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    /**
     * 写入数据到bytes中
     */
    public int writeTo(WritableByteChannel channel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.write(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    /**
     * 从SocketChannel读取数据
     */
    public int readFrom(SocketChannel channel) throws IOException {
        ByteBuffer buffer = this.buffer;
        int bytesProduced = 0;
        int len;

        // 读取或写数据到Socket原理
        // 回调当前可读、可写时我们进行数据填充或者消费
        // 但是过程中可能SocketChannel资源被其他SocketChannel占用了资源
        // 那么我们应该让出当前的线程调度，让应该得到数据消费的SocketChannel的到CPU调度
        // 而不应该单纯的buffer.hasRemaining()判断
        do {
            len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException("Cannot read any data with:" + channel);
            }
            bytesProduced += len;
        } while (buffer.hasRemaining() && len != 0);

        return bytesProduced;
    }

    /**
     * 写数据到SocketChannel
     */
    public int writeTo(SocketChannel channel) throws IOException {
        ByteBuffer buffer = this.buffer;
        int bytesProduced = 0;
        int len;

        // 读取或写数据到Socket原理
        // 回调当前可读、可写时我们进行数据填充或者消费
        // 但是过程中可能SocketChannel资源被其他SocketChannel占用了资源
        // 那么我们应该让出当前的线程调度，让应该得到数据消费的SocketChannel的到CPU调度
        // 而不应该单纯的buffer.hasRemaining()判断
        do {
            len = channel.write(buffer);
            if (len < 0) {
                throw new EOFException("Current write any data with:" + channel);
            }
            bytesProduced += len;
        } while (buffer.hasRemaining() && len != 0);

        return bytesProduced;
    }

    /**
     * 开始写入数据到IoArgs
     */
    public void startWriting() {
        buffer.clear();
        // 定义容纳区间
        buffer.limit(limit);
    }

    /**
     * 写完数据后调用
     */
    public void finishWriting() {
        buffer.flip();
    }

    /**
     * 设置单次写操作的容纳区间
     *
     * @param limit 区间大小
     */
    public void limit(int limit) {
        this.limit = Math.min(limit, buffer.capacity());
    }

    /**
     * 重置最大限制
     */
    public void resetLimit() {
        this.limit = buffer.capacity();
    }

    /**
     * 获取当前的容量
     *
     * @return 容量
     */
    public int capacity() {
        return buffer.capacity();
    }

    /**
     * 是否还有数据需要消费，或者说是否还有空闲区间需要容纳内容
     *
     * @return 还有数据存储或未消费区间
     */
    public boolean remained() {
        return buffer.remaining() > 0;
    }

    /**
     * 是否需要填满 或 完全消费所有数据
     *
     * @return 是否
     */
    public boolean isNeedConsumeRemaining() {
        return isNeedConsumeRemaining;
    }

    /**
     * 填充数据
     *
     * @param size 想要填充数据的长度
     * @return 真实填充数据的长度
     */
    public int fillEmpty(int size) {
        int fillSize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + fillSize);
        return fillSize;
    }

    /**
     * 清空部分数据
     *
     * @param size 想要清空的数据长度
     * @return 真实清空的数据长度
     */
    public int setEmpty(int size) {
        int emptySize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + emptySize);
        return emptySize;
    }


    /**
     * IoArgs 提供者、处理者；数据的生产或消费者
     */
    public interface IoArgsEventProcessor {
        /**
         * 提供一份可消费的IoArgs
         *
         * @return IoArgs
         */
        IoArgs provideIoArgs();

        /**
         * 消费失败时回调
         *
         * @param e 异常信息
         * @return 是否关闭链接，True关闭
         */
        boolean onConsumeFailed(Throwable e);

        /**
         * 消费成功
         *
         * @param args IoArgs
         * @return True:直接注册下一份调度，False:无需注册
         */
        boolean onConsumeCompleted(IoArgs args);
    }
}
