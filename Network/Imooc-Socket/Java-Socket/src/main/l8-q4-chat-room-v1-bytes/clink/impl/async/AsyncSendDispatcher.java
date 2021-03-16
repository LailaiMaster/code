package clink.impl.async;

import java.io.IOException;
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

    private final IoArgs mIoArgs = new IoArgs();

    /**
     * 发送包的队列
     */
    private final Queue<SendPacket> mSendPacketQueue = new ConcurrentLinkedQueue<>();

    /**
     * 当前正在发送的包
     */
    private SendPacket mSendingPacket;

    /**
     * 当前正在发送的包的总长度
     */
    private int mTotal;

    /**
     * 当前正在发送的包的已发送长度
     */
    private int mPosition;

    public AsyncSendDispatcher(Sender sender) {
        mSender = sender;
    }

    @Override
    public void send(SendPacket packet) {
        //加入到队列中
        mSendPacketQueue.offer(packet);
        //尝试启动发送
        if (mIsSending.compareAndSet(false, true)) {
            //没有正在发送，才去发送
            sendNextMessage();
        }
    }

    /**
     * @return 如果没有了，就返回 null。
     */
    private SendPacket takePacket() {
        SendPacket sendPacket = mSendPacketQueue.poll();
        //已经取消的包就不发送了
        if (sendPacket != null && sendPacket.isCanceled()) {
            return takePacket();
        }
        return sendPacket;
    }

    /**
     * 选取需要发送的下一个数据包
     */
    private void sendNextMessage() {
        //容错处理：如果之前的消息还存在，就先释放掉（一般不会发生这种情况）
        SendPacket sendingPacket = mSendingPacket;
        if (sendingPacket != null) {
            CloseUtils.close(sendingPacket);
        }

        //取下一个包，并赋值给 mSendingPacket
        SendPacket packet = mSendingPacket = takePacket();
        //队列为空，停止发送
        if (packet == null) {
            mIsSending.set(false);
            return;
        }

        mTotal = packet.getLength();
        mPosition = 0;
        System.out.println("AsyncSendDispatcher.sendNextMessage mTotal = " + mTotal + " mPosition = " + mPosition);

        //开始发送当前包
        sendCurrentMessage();
    }

    /**
     * 这是一个包含状态方法：根据 mPosition >= mTotal 对比是继续发送当前的包，还是发送新的包。
     */
    private void sendCurrentMessage() {
        IoArgs ioArgs = mIoArgs;

        //清理、复位
        mIoArgs.startWriting();

        if (mPosition >= mTotal) {//写完了则尝试发送下一个包
            sendNextMessage();
            return;
        } else if (mPosition == 0) {//新包：需要写头部
            ioArgs.writeLength(mSendingPacket.getLength());
        }

        //开始写数据
        byte[] bytes = mSendingPacket.bytes();
        //从 bytes 读取数据到 ioArgs 中
        int readCount = ioArgs.readFrom(bytes, mPosition);
        mPosition += readCount;

        //切换到写模式
        mIoArgs.finishWriting();

        try {
            // 调用 Sender 发送 IoArgs 读取到的数据。
            // Sender 保证把 IoArgs 中的数据发送完才回调 IoArgsEventListener，暂时不考虑发送失败的情况。
            // 这个 IoArgs 发送完成后，将在回调传入的 mIoArgsEventListener 的 onCompleted 方法，然后继续调用 sendCurrentMessage。
            mSender.sendAsync(mIoArgs, mIoArgsEventListener);
        } catch (IOException e) {
            e.printStackTrace();
            closeAndNotify();
        }

    }

    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    @Override
    public void cancel(SendPacket packet) {
        //no op
    }

    @Override
    public void close() {
        if (mIsClosed.compareAndSet(false, true)) {
            mIsSending.set(false);
            SendPacket sendingPacket = mSendingPacket;
            if (sendingPacket != null) {
                CloseUtils.close(sendingPacket);
                mSendingPacket = null;
            }
        }
    }

    private final IoArgs.IoArgsEventListener mIoArgsEventListener = new IoArgs.IoArgsEventListener() {
        @Override
        public void onStarted(IoArgs args) {
            //开始发送，不需要做处理
        }

        @Override
        public void onCompleted(IoArgs args) {
            /*写完了一个 IoArgs，再继续写*/
            sendCurrentMessage();
        }
    };

}
