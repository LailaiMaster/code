package clink.impl.async;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import clink.core.IoArgs;
import clink.core.SendDispatcher;
import clink.core.SendPacket;
import clink.core.Sender;
import clink.utils.CloseUtils;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/18 17:35
 */
public class AsyncSendDispatcher implements SendDispatcher {

    private final Sender mSender;
    private final AtomicBoolean mIsSending = new AtomicBoolean(false);
    private final AtomicBoolean mIsClosed = new AtomicBoolean(false);

    /**
     * 发送包的队列
     */
    private final Queue<SendPacket> mSendPacketQueue = new ConcurrentLinkedQueue<>();

    /**
     * 当前正在发送的包
     */
    private SendPacket<?> mSendingPacket;
    /**
     * 当前正在发送的通道
     */
    private ReadableByteChannel mSendingReadableByteChannel;
    /**
     * 当前正在发送的包的总长度
     */
    private long mTotal;
    /**
     * 当前正在发送的包的已发送长度
     */
    private int mPosition;

    private IoArgs mIoArgs = new IoArgs();

    public AsyncSendDispatcher(Sender sender) {
        mSender = sender;
        mSender.setSendListener(newIoArgsEventProcessor());
    }

    @Override
    public void send(SendPacket packet) {
        //加入到队列中
        mSendPacketQueue.offer(packet);
        //尝试发送数据
        if (mIsSending.compareAndSet(false, true)) {
            sendNextMessage();
        }
    }

    private SendPacket takePacket() {
        SendPacket sendPacket = mSendPacketQueue.poll();
        //已经取消的包就不发送了
        if (sendPacket != null && sendPacket.isCanceled()) {
            return takePacket();
        }
        return sendPacket;
    }

    /*选取需要发送的下一个数据包*/
    private void sendNextMessage() {
        //如果之前的包不为 null，则先关闭（一般情况下不会发送）
        SendPacket sendingPacket = mSendingPacket;
        if (sendingPacket != null) {
            CloseUtils.close(sendingPacket);
        }

        /*从队列中取一个包*/
        SendPacket packet = mSendingPacket = takePacket();

        //队列为空，停止发送
        if (packet == null) {
            mIsSending.set(false);
            return;
        }

        //初始化长度
        mTotal = packet.getLength();
        mPosition = 0;
        //开始发送
        sendCurrentMessage();
    }

    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    @Override
    public void cancel(SendPacket packet) {
        //暂时没有处理
    }

    @Override
    public void close() {
        if (mIsClosed.compareAndSet(false, true)) {
            mIsSending.set(false);
            completePacket(false);
        }
    }

    private void sendCurrentMessage() {
        //如果写完了则处理写完的包，再发送接下里的包
        if (mPosition >= mTotal) {
            completePacket(mPosition == mTotal);
            sendNextMessage();
            return;
        }

        //如果没有写完，则调用方法，表示希望开始发送数据
        try {
            mSender.postSendAsync();
        } catch (IOException e) {
            e.printStackTrace();
            closeAndNotify();
        }
    }


    private IoArgs.IoArgsEventProcessor newIoArgsEventProcessor() {
        return new IoArgs.IoArgsEventProcessor() {

            @Override
            public IoArgs provideIoArgs() {
                IoArgs ioArgs = mIoArgs;
                //用 mSendingReadableByteChannel 是否为 null 判断是否为新的包
                if (mSendingReadableByteChannel == null) {//新的包开始写，写头部
                    mSendingReadableByteChannel = Channels.newChannel(mSendingPacket.open());
                    ioArgs.limit(4);//约定头部为 4 个字节，表示包的长度
                    ioArgs.writeLength((int) mSendingPacket.getLength());
                } else /*旧的包，则继续写数据*/ {
                    try {
                        mIoArgs.limit((int) Math.min(mTotal - mPosition, mIoArgs.capacity()));
                        //从 Channel 读取数据到 ioArgs 中，提供给外部进行发送。
                        int readCount = mIoArgs.readFrom(mSendingReadableByteChannel);
                        mPosition += readCount;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                return ioArgs;
            }

            @Override
            public void consumeFailed(IoArgs ioArgs, Exception e) {
                //暂时不处理
                e.printStackTrace();
            }

            @Override
            public void onConsumeCompleted(IoArgs args) {
                /*写完了一个IoArgs，再继续写*/
                sendCurrentMessage();
            }

        };
    }

    /**
     * 完成Packet发送
     *
     * @param isSucceed 是否成功
     */
    private void completePacket(@SuppressWarnings("unused") boolean isSucceed) {
        SendPacket packet = this.mSendingPacket;

        if (packet == null) {
            return;
        }

        CloseUtils.close(packet, mSendingReadableByteChannel);

        mSendingPacket = null;
        mSendingReadableByteChannel = null;
        mTotal = 0;
        mPosition = 0;
    }

}
