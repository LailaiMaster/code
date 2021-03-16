package clink.impl.async;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import clink.core.IoArgs;
import clink.core.ReceiveDispatcher;
import clink.core.ReceivePacket;
import clink.core.Receiver;
import clink.utils.CloseUtils;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 17:00
 */
public class AsyncReceiveDispatcher implements ReceiveDispatcher {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final Receiver receiver;

    private final ReceivePacketCallback receivePacketCallback;

    private final AsyncPacketWriter.PacketProvider packetProvider = new AsyncPacketWriter.PacketProvider() {

        /**
         * 构建Packet操作，根据类型、长度构建一份用于接收数据的Packet
         */
        @Override
        public ReceivePacket takePacket(byte type, long length, byte[] headerInfo) {
            return receivePacketCallback.onArrivedNewPacket(type, length, headerInfo);
        }

        @Override
        public void completedPacket(ReceivePacket packet, boolean isSucceed) {
            CloseUtils.close(packet);
            receivePacketCallback.onReceivePacketCompleted(packet);
        }

        @Override
        public void onReceivedHeartbeat() {
            receivePacketCallback.onReceivedHeartbeat();
        }

    };

    private final AsyncPacketWriter asyncPacketWriter = new AsyncPacketWriter(packetProvider);

    public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallback receivePacketCallback) {
        this.receiver = receiver;
        this.receiver.setReceiveListener(ioArgsEventProcessor);
        this.receivePacketCallback = receivePacketCallback;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {
        registerReceive();
    }

    private void registerReceive() {
        try {
            receiver.postReceiveAsync();
        } catch (IOException e) {
            e.printStackTrace();
            closeAndNotify();
        }
    }

    private final IoArgs.IoArgsEventProcessor ioArgsEventProcessor = new IoArgs.IoArgsEventProcessor() {

        /**
         * 网络接收就绪，此时可以读取数据，需要返回一个容器用于容纳数据
         *
         * @return 用以容纳数据的IoArgs
         */
        @Override
        public IoArgs provideIoArgs() {
            IoArgs args = asyncPacketWriter.takeIoArgs();
            // 一份新的IoArgs需要调用一次开始写入数据的操作
            args.startWriting();
            return args;
        }

        /**
         * 数据接收成功
         *
         * @param args IoArgs
         */
        @Override
        public void onConsumeCompleted(IoArgs args) {
            if (isClosed.get()) {
                return;
            }

            // 消费数据之前标示args数据填充完成，改变为可读取数据状态。
            args.finishWriting();

            do {
                asyncPacketWriter.consumeIoArgs(args);
            } while (args.remained() && !isClosed.get());
            //再次注册
            registerReceive();
        }

        @Override
        public void onConsumeFailed(IoArgs ioArgs, Exception e) {
            e.printStackTrace();
        }

    };

    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            asyncPacketWriter.close();
        }
    }

}