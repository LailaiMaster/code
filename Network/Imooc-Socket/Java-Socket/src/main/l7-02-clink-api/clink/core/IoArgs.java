package clink.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import clink.utils.CharUtils;

/**
 * IO 参数，用于执行实际的异步读写操作，读写操作状态将会以异步回调的形式通知。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/8 22:34
 */
public class IoArgs {

    private final byte[] byteBuffer = new byte[256];
    private final ByteBuffer buffer = ByteBuffer.wrap(byteBuffer);

    public int read(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        return socketChannel.read(buffer);
    }

    public int write(SocketChannel socketChannel) throws IOException {
        return socketChannel.write(buffer);
    }

    public String bufferString() {
        return new String(byteBuffer, 0, buffer.position() - CharUtils.LINE_BREAK_LENGTH /*丢弃换行符*/);
    }

    /**
     * 读写事件的监听者，谁要用 IoArgs，就要实现它。
     */
    public interface IoArgsEventListener {

        /**
         * 回调时，表示一次 Io 操作就绪
         */
        void onStarted(IoArgs args);

        /**
         * 回调时，表示一次 Io 操作完成
         */
        void onCompleted(IoArgs args);
    }

}
