package net.qiujuer.library.clink.impl.async;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.SendDispatcher;
import net.qiujuer.library.clink.core.SendPacket;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.impl.exceptions.EmptyIoArgsException;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncSendDispatcher implements SendDispatcher,
        IoArgs.IoArgsEventProcessor, AsyncPacketReader.PacketProvider {
    private final Sender sender;
    // 阻塞队列，默认等待任务数量设定为16个；超过16个添加任务将被阻塞等待
    private final BlockingQueue<SendPacket> queue = new ArrayBlockingQueue<>(16);
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AsyncPacketReader reader = new AsyncPacketReader(this);

    public AsyncSendDispatcher(Sender sender) {
        this.sender = sender;
        sender.setSendListener(this);
    }

    /**
     * 发送Packet
     * 首先添加到队列，如果当前状态为未启动发送状态
     * 则，尝试让reader提取一份packet进行数据发送
     * <p>
     * 如果提取数据后reader有数据，则进行异步输出注册
     *
     * @param packet 数据
     */
    @Override
    public void send(SendPacket packet) {
        try {
            queue.put(packet);
            requestSend(false);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 发送心跳帧，将心跳帧放到帧发送队列进行发送
     */
    @Override
    public void sendHeartbeat() {
        if (!queue.isEmpty()) {
            return;
        }
        if (reader.requestSendHeartbeatFrame()) {
            requestSend(false);
        }
    }

    /**
     * 取消Packet操作
     * 如果还在队列中，代表Packet未进行发送，则直接标志取消，并返回即可
     * 如果未在队列中，则让reader尝试扫描当前发送序列，查询是否当前Packet正在发送
     * 如果是则进行取消相关操作
     *
     * @param packet 数据
     */
    @Override
    public void cancel(SendPacket packet) {
        boolean ret = queue.remove(packet);
        if (ret) {
            packet.cancel();
            return;
        }

        reader.cancel(packet);
    }

    /**
     * reader从当前队列中提取一份Packet
     *
     * @return 如果队列有可用于发送的数据则返回该Packet
     */
    @Override
    public SendPacket takePacket() {
        SendPacket packet = queue.poll();
        if (packet == null) {
            return null;
        }

        if (packet.isCanceled()) {
            // 已取消，不用发送
            return takePacket();
        }
        return packet;
    }

    /**
     * 完成Packet发送
     *
     * @param isSucceed 是否成功
     */
    @Override
    public void completedPacket(SendPacket packet, boolean isSucceed) {
        CloseUtils.close(packet);
    }

    /**
     * 请求网络进行数据发送
     */
    private void requestSend(boolean callFromIoConsume) {
        synchronized (isSending) {
            final AtomicBoolean isRegisterSending = isSending;
            final boolean oldState = isRegisterSending.get();
            if (isClosed.get() || (oldState && !callFromIoConsume)) {
                // 已关闭
                // 从非IO流程调用需检测是否已注册
                return;
            }

            // 从IO流程调用时，当前状态应处于发送中才对
            if (callFromIoConsume && !oldState) {
                throw new IllegalStateException("Call from IoConsume, current state should in sending!");
            }

            // 返回True代表当前有数据需要发送
            if (reader.requestTakePacket()) {
                // 预先设置，防止调用 sender.postSendAsync() 可能会触发递归流程
                isRegisterSending.set(true);
                try {
                    // 真实注册
                    sender.postSendAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                    CloseUtils.close(this);
                }
            } else {
                // 无需注册时，设置未在发送中
                isRegisterSending.set(false);
            }
        }
    }

    /**
     * 关闭操作，关闭自己同时需要关闭reader
     */
    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            // reader关闭
            reader.close();
            // 清理队列
            queue.clear();
            // 设置当前发送状态
            synchronized (isSending) {
                isSending.set(false);
            }
        }
    }

    /**
     * 网络发送就绪回调，当前已进入发送就绪状态，等待填充数据进行发送
     * 此时从reader中填充数据，并进行后续网络发送
     *
     * @return NULL，可能填充异常，或者想要取消本次发送
     */
    @Override
    public IoArgs provideIoArgs() {
        return isClosed.get() ? null : reader.fillData();
    }

    /**
     * 网络发送IoArgs出现异常
     *
     * @param e 异常信息
     */
    @Override
    public boolean onConsumeFailed(Throwable e) {
        if (e instanceof EmptyIoArgsException) {
            // args为null异常，此时直接进行对应检查即可
            requestSend(true);
            return false;
        } else {
            // 其他异常信息，直接中断流程
            CloseUtils.close(this);
            return true;
        }
    }

    /**
     * 网络发送IoArgs完成回调
     * 在该方法进行reader对当前队列的Packet提取，并进行后续的数据发送注册
     *
     * @param args IoArgs
     */
    @Override
    public boolean onConsumeCompleted(IoArgs args) {
        synchronized (isSending) {
            AtomicBoolean isRegisterSending = isSending;
            final boolean isRunning = !isClosed.get();
            // 从IO流程调用时，当前状态应处于发送中才对
            if (!isRegisterSending.get() && isRunning) {
                throw new IllegalStateException("Call from IoConsume, current state should in sending!");
            }

            // 设置新状态
            isRegisterSending.set(isRunning && reader.requestTakePacket());

            // 返回状态
            return isRegisterSending.get();
        }
    }
}
