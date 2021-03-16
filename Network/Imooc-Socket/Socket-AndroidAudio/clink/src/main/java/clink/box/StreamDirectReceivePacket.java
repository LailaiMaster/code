package clink.box;


import clink.core.Packet;
import clink.core.ReceivePacket;

import java.io.OutputStream;

/**
 * 直流接收 Packet。
 */
public class StreamDirectReceivePacket extends ReceivePacket<OutputStream, OutputStream> {

    private OutputStream outputStream;

    public StreamDirectReceivePacket(OutputStream outputStream, long length) {
        super(length);
        // 用以读取数据进行输出的输入流。
        this.outputStream = outputStream;
    }

    @Override
    public byte getType() {
        return Packet.TYPE_STREAM_DIRECT;
    }


    @Override
    protected OutputStream createStream() {
        return outputStream;
    }

    /**
     * 对于直流而言，不知道要输出什么类型，那就直接返回 OutputStream，让业务层去操作直接的流数据。
     */
    @Override
    protected OutputStream buildEntity(OutputStream stream) {
        return outputStream;
    }

}
