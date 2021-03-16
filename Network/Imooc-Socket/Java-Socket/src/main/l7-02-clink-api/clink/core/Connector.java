package clink.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import clink.impl.SocketChannelAdapter;

/**
 * 代表一个 SocketChannel 连接，用于调用 Sender 和  Receiver 执行读写操作。【现在只实现了接收端】
 */
public class Connector implements Closeable {

    /**
     * 该连接的唯一标识
     */
    private final UUID key = UUID.randomUUID();

    /**
     * 实际的连接
     */
    private SocketChannel channel;

    /**
     * 数据发送者
     */

    private Sender sender;

    /**
     * 数据接收者
     */
    private Receiver receiver;

    private final SocketChannelAdapter.OnChannelStatusChangedListener mOnChannelStatusChangedListener = channel -> {
        //no op
    };

    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;

        IoContext ioContext = IoContext.get();
        SocketChannelAdapter socketChannelAdapter = new SocketChannelAdapter(channel, ioContext.getIoProvider(), mOnChannelStatusChangedListener);

        this.sender = socketChannelAdapter;
        this.receiver = socketChannelAdapter;

        readNextMessage();
    }

    /*开始读取消息*/
    private void readNextMessage() {
        if (receiver != null) {
            try {
                receiver.receiveAsync(echoReceiveListener);
            } catch (IOException e) {
                System.out.println("Connector 开始接收数据异常：" + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        //no op
    }

    private final IoArgs.IoArgsEventListener echoReceiveListener = new IoArgs.IoArgsEventListener() {

        @Override
        public void onStarted(IoArgs args) {
        }

        @Override
        public void onCompleted(IoArgs args) {
            //通知新消息到达
            onReceiveNewMessage(args.bufferString());
            //读完继续读下一条
            readNextMessage();
        }
    };

    /**
     * 链接关闭时的回调
     */
    protected void onChannelClosed(SocketChannel channel) {

    }

    /**
     * 新消息到达时的回调
     */
    protected void onReceiveNewMessage(String newMessage) {
        System.out.println("Connector: " + key.toString() + ": " + newMessage);
    }

}