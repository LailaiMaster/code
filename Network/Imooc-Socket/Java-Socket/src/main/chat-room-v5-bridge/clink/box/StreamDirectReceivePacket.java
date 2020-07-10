package clink.box;

import java.io.OutputStream;

import clink.core.Packet;
import clink.core.ReceivePacket;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/9/22 16:58
 */
public class StreamDirectReceivePacket extends ReceivePacket<OutputStream, OutputStream> {

    private OutputStream mOutputStream;

    public StreamDirectReceivePacket(OutputStream outputStream, long len) {
        super(len);
        this.mOutputStream = outputStream;
    }

    @Override
    protected OutputStream buildEntity(OutputStream stream) {
        //用以读取数据进行输出的输入流
        return mOutputStream;
    }

    @Override
    public byte getType() {
        return Packet.TYPE_STREAM_DIRECT;
    }

    @Override
    protected OutputStream createStream() {
        return mOutputStream;
    }

}