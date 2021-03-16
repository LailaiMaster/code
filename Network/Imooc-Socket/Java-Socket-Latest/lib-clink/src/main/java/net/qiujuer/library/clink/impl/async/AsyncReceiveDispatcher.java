package net.qiujuer.library.clink.impl.async;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.ReceiveDispatcher;
import net.qiujuer.library.clink.core.ReceivePacket;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 接收调度
 */
public class AsyncReceiveDispatcher implements ReceiveDispatcher,
        IoArgs.IoArgsEventProcessor, AsyncPacketWriter.PacketProvider {
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final Receiver receiver;
    private final ReceivePacketCallback callback;

    private final AsyncPacketWriter writer = new AsyncPacketWriter(this);

    public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallback callback) {
        this.receiver = receiver;
        this.receiver.setReceiveListener(this);
        this.callback = callback;
    }

    /**
     * 开始进入接收方法
     */
    @Override
    public void start() {
        registerReceive();
    }

    /**
     * 停止接收数据
     */
    @Override
    public void stop() {
        receiver.setReceiveListener(null);
    }

    /**
     * 关闭操作，关闭相关流
     */
    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            writer.close();
            receiver.setReceiveListener(null);
        }
    }

    /**
     * 注册接收数据
     */
    private void registerReceive() {
        try {
            receiver.postReceiveAsync();
        } catch (Exception e) {
            CloseUtils.close(this);
        }
    }

    /**
     * 网络接收就绪，此时可以读取数据，需要返回一个容器用于容纳数据
     *
     * @return 用以容纳数据的IoArgs
     */
    @Override
    public IoArgs provideIoArgs() {
        IoArgs ioArgs = writer.takeIoArgs();
        // 一份新的IoArgs需要调用一次开始写入数据的操作
        ioArgs.startWriting();
        return ioArgs;
    }

    /**
     * 接收数据失败
     *
     * @param e 异常信息
     */
    @Override
    public boolean onConsumeFailed(Throwable e) {
        // args 不会为null，当出现异常时，需要关闭链接
        CloseUtils.close(this);
        return true;
    }

    /**
     * 接收数据成功
     *
     * @param args IoArgs
     */
    @Override
    public boolean onConsumeCompleted(IoArgs args) {
        final AtomicBoolean isClosed = this.isClosed;
        final AsyncPacketWriter writer = this.writer;

        // 消费数据之前标示args数据填充完成，
        // 改变未可读取数据状态
        args.finishWriting();

        // 有数据则重复消费
        do {
            writer.consumeIoArgs(args);
        } while (args.remained() && !isClosed.get());

        // 如果没有关闭则直接注册下一次监听
        return !isClosed.get();
    }

    /**
     * 构建Packet操作，根据类型、长度构建一份用于接收数据的Packet
     */
    @Override
    public ReceivePacket takePacket(byte type, long length, byte[] headerInfo) {
        return callback.onArrivedNewPacket(type, length, headerInfo);
    }

    /**
     * 当Packet接收数据完成或终止时回调
     *
     * @param packet    接收包
     * @param isSucceed 是否成功接收完成
     */
    @Override
    public void completedPacket(ReceivePacket packet, boolean isSucceed) {
        CloseUtils.close(packet);
        callback.onReceivePacketCompleted(packet);
    }

    /**
     * 当收到心跳包时直接往外抛出到Connector
     */
    @Override
    public void onReceivedHeartbeat() {
        callback.onReceivedHeartbeat();
    }
}
