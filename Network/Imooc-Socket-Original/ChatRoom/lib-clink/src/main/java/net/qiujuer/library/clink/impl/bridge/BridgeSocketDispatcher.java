package net.qiujuer.library.clink.impl.bridge;

import net.qiujuer.library.clink.core.*;
import net.qiujuer.library.clink.impl.exceptions.EmptyIoArgsException;
import net.qiujuer.library.clink.utils.plugin.CircularByteBuffer;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 桥接调度器实现
 * 当前调度器同时实现了发送者与接受者调度逻辑
 * 核心思想为：把接受者接收到的数据全部转发给发送者
 */
public class BridgeSocketDispatcher implements ReceiveDispatcher, SendDispatcher {
    private final CircularByteBuffer buffer = new CircularByteBuffer(512, true);
    private final Receiver receiver;
    private final Sender sender;
    private final SendEventProcessor sendEventProcessor;

    public BridgeSocketDispatcher(Receiver receiver, Sender sender) {
        this.receiver = receiver;
        this.sender = sender;

        receiver.setReceiveListener(new ReceiverEventProcessor(buffer));
        sender.setSendListener(sendEventProcessor = new SendEventProcessor(sender, buffer));
    }

    /**
     * 外部初始化好了桥接调度器后需要调用start方法开始
     */
    @Override
    public void start() {
        // nothing
        requestReceive();
    }

    @Override
    public void stop() {
        // nothing
        receiver.setReceiveListener(null);
        sender.setSendListener(null);
    }

    @Override
    public void send(SendPacket packet) {
        // nothing
    }

    @Override
    public void sendHeartbeat() {
        // nothing
    }

    @Override
    public void cancel(SendPacket packet) {
        // nothing
    }

    @Override
    public void close() {
        // nothing
    }

    private synchronized void requestReceive() {
        try {
            receiver.postReceiveAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSpaceReading() {
        return buffer.getAvailable();
    }

    private int getSpaceWriting() {
        return buffer.getSpaceLeft();
    }

    private class ReceiverEventProcessor implements IoArgs.IoArgsEventProcessor {
        private final WritableByteChannel writableByteChannel;
        private final IoArgs ioArgs = new IoArgs(256, false);

        private ReceiverEventProcessor(CircularByteBuffer buffer) {
            this.writableByteChannel = Channels.newChannel(buffer.getOutputStream());
        }

        @Override
        public IoArgs provideIoArgs() {
            final int spaceWriting = getSpaceWriting();
            if (spaceWriting > 0) {
                IoArgs ioArgs = this.ioArgs;
                ioArgs.limit(spaceWriting);
                ioArgs.startWriting();
                return ioArgs;
            }
            return null;
        }

        @Override
        public boolean onConsumeFailed(Throwable e) {
            if (e instanceof EmptyIoArgsException) {
                sendEventProcessor.requestSend();
                requestReceive();
                return false;
            } else if (e instanceof IOException) {
                return true;
            } else {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean onConsumeCompleted(final IoArgs args) {
            args.finishWriting();
            try {
                if (args.remained()) {
                    args.writeTo(writableByteChannel);
                }

                // 接收数据后请求发送数据
                sendEventProcessor.requestSend();

                // 继续接收数据
                return true;
            } catch (IOException e) {
                // 不再接收
                return false;
            }
        }
    }

    private class SendEventProcessor implements IoArgs.IoArgsEventProcessor {
        private final AtomicBoolean isSending = new AtomicBoolean();
        private final IoArgs ioArgs = new IoArgs(256);
        private final ReadableByteChannel readableByteChannel;
        private final Sender sender;

        private SendEventProcessor(Sender sender, CircularByteBuffer buffer) {
            this.sender = sender;
            this.readableByteChannel = Channels.newChannel(buffer.getInputStream());
        }

        @Override
        public IoArgs provideIoArgs() {
            BridgeIllegalStateException.check(isSending.get());
            final int spaceReading = getSpaceReading();
            if (spaceReading > 0) {
                final IoArgs args = this.ioArgs;
                args.limit(spaceReading);
                args.startWriting();
                try {
                    args.readFrom(readableByteChannel);
                    args.finishWriting();
                    return args;
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            return null;
        }

        @Override
        public boolean onConsumeFailed(Throwable e) {
            BridgeIllegalStateException.check(isSending.compareAndSet(true, false));
            if (e instanceof EmptyIoArgsException) {
                requestSend();
                // 无需关闭链接
                return false;
            } else if (e instanceof IOException) {
                // 关闭链接
                return true;
            } else {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean onConsumeCompleted(IoArgs args) {
            final int spaceReading = getSpaceReading();
            if (spaceReading > 0) {
                return isSending.get();
            } else {
                isSending.set(false);
                return false;
            }
        }


        /**
         * 请求网络进行数据发送
         */
        synchronized void requestSend() {
            final AtomicBoolean isSending = this.isSending;
            final Sender sender = this.sender;
            if (sender != BridgeSocketDispatcher.this.sender || isSending.get()) {
                return;
            }

            final int spaceReading = getSpaceReading();
            if (spaceReading > 0 && isSending.compareAndSet(false, true)) {
                try {
                    sender.postSendAsync();
                } catch (Exception e) {
                    BridgeIllegalStateException.check(isSending.compareAndSet(true, false));
                }
            }
        }
    }
}
