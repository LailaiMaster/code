package clink.core;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * IO参数，用于执行实际的异步读写操作，读写操作状态将会以异步回调的形式通知。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/8 22:34
 */
@SuppressWarnings("Duplicates")
public class IoArgs {

    private int limit = 256;

    private final ByteBuffer buffer;

    /**
     * 是否需要消费所有的区间（读取、写入），主要用于直流的不定长数据包。
     */
    private final boolean isNeedConsumeRemaining;

    public IoArgs() {
        this(256);
    }

    public IoArgs(int size) {
        this(size, true);
    }

    /**
     * @param isNeedConsumeRemaining 可以只接收部分数据就返回。
     */
    public IoArgs(int size, boolean isNeedConsumeRemaining) {
        this.limit = size;
        this.isNeedConsumeRemaining = isNeedConsumeRemaining;
        this.buffer = ByteBuffer.allocate(size);
    }

    /**
     * 从 bytes 数组中读取数据到 IoArgs 中。
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
     * 将 IoArgs 中的数据写入到 bytes 中。
     */
    public int writeTo(byte[] bytes, int offset) {
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.get(bytes, offset, size);
        return size;
    }

    /**
     * 从 readableByteChannel 中读取数据。【保证 IoArgs 读满】
     */
    public int readFrom(ReadableByteChannel readableByteChannel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int writeLength = readableByteChannel.read(buffer);
            if (writeLength < 0) {//无法读取到更多的数据
                throw new EOFException();
            }
            bytesProduced += writeLength;
        }
        return bytesProduced;
    }

    /**
     * 把数据写入到 writableByteChannel 中。【保证 IoArgs 中数据全部写完】
     */
    public int writeTo(WritableByteChannel writableByteChannel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int writeLength = writableByteChannel.write(buffer);
            if (writeLength < 0) {//无法读取到更多的数据
                throw new EOFException();
            }
            bytesProduced += writeLength;
        }
        return bytesProduced;
    }

    /**
     * 从 SocketChannel 读取数据，直到不可读为止。【用于 {@link clink.impl.SocketChannelAdapter} 中的非阻塞读】
     */
    public int readFrom(SocketChannel socketChannel) throws IOException {
        ByteBuffer localBuffer = this.buffer;
        int bytesProduced = 0;
        int len;
        /*
        读取或写数据到Socket原理：
                回调当前可读、可写时我们进行数据填充或者消费，但是过程中可能 SocketChannel 资源被其他 SocketChannel 占用了资源（网卡把资源让给了另外一个 SocketChannel）
                那么我们应该让出当前的线程调度，让应该得到数据消费的 SocketChannel 的到 CPU 调度，而不应该单纯的 buffer.hasRemaining() 判断。
         */
        do {
            len = socketChannel.read(localBuffer);
            if (len < 0) {//无法读取到更多的数据
                //Selector 选择后却又读不到数据，说明连接出问题了
                throw new EOFException("Cannot read any data with:" + socketChannel);
            }
            bytesProduced += len;
        } while (localBuffer.hasRemaining() && len != 0/*说明该通道现在不能读了*/);

        return bytesProduced;
    }

    /**
     * 写数据到 SocketChannel，直到不可写为止。【用于 {@link clink.impl.SocketChannelAdapter} 中的非阻塞写】
     */
    public int writeTo(SocketChannel socketChannel) throws IOException {
        int bytesProduced = 0;
        ByteBuffer localBuffer = this.buffer;
        int writeLength;
        /*
        读取或写数据到Socket原理：
                回调当前可读、可写时我们进行数据填充或者消费，但是过程中可能 SocketChannel 资源被其他 SocketChannel 占用了资源（网卡把资源让给了另外一个 SocketChannel）
                那么我们应该让出当前的线程调度，让应该得到数据消费的 SocketChannel 的到 CPU 调度，而不应该单纯的 buffer.hasRemaining() 判断。
         */
        do {
            writeLength = socketChannel.write(localBuffer);
            if (writeLength < 0) {//无法读取到更多的数据
                //Selector 选择后却又写不出数据，说明连接出问题了
                throw new EOFException("Current write any data with:" + socketChannel);
            }
            bytesProduced += writeLength;
        } while (localBuffer.hasRemaining() && writeLength != 0/*说明该通道现在不能写了*/);

        return bytesProduced;
    }

    public void startWriting() {
        //清理，开始写入数据
        buffer.clear();
        // 定义容纳区间
        buffer.limit(limit);
    }

    public void finishWriting() {
        //切换到读取模式
        buffer.flip();
    }

    /**
     * 设置本次读取数据的大小
     */
    public void limit(int receiveSize) {
        limit = Math.min(receiveSize, buffer.capacity());
    }

    /**
     * 重置最大限制
     */
    public void resetLimit() {
        this.limit = buffer.capacity();
    }

    public int readLength() {
        return buffer.getInt();
    }

    public int capacity() {
        return buffer.capacity();
    }

    public boolean remained() {
        return buffer.remaining() > 0;
    }

    /**
     * 是否需要填满 或 完全消费所有数据。
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
        //直接修改position
        buffer.position(buffer.position() + fillSize);
        return fillSize;
    }

    /**
     * 填充空数据
     */
    public int setEmpty(int size) {
        int fillSize = Math.min(size, buffer.remaining());
        //直接修改 position。
        buffer.position(buffer.position() + fillSize);
        return fillSize;
    }

    /**
     * IoArgs 提供者、处理者；数据的生产或消费者。定义为这种形式，用于异步处理 IO。
     */
    public interface IoArgsEventProcessor {

        /**
         * 提供一份可消费的IoArgs
         */
        IoArgs provideIoArgs();

        /**
         * 消费失败时回调
         *
         * @param ioArgs IoArgs
         * @param e      异常信息
         */
        void onConsumeFailed(IoArgs ioArgs, Exception e);


        /**
         * 消费成功
         *
         * @param args IoArgs
         */
        void onConsumeCompleted(IoArgs args);
    }

    @Override
    public String toString() {
        return "IoArgs{" +
                "limit=" + limit +
                ", buffer=" + buffer +
                ", isNeedConsumeRemaining=" + isNeedConsumeRemaining +
                '}';
    }

}