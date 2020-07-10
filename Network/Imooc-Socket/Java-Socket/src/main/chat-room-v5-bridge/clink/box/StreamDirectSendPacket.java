package clink.box;

import java.io.InputStream;

import clink.core.Packet;
import clink.core.SendPacket;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/9/22 16:39
 */
public class StreamDirectSendPacket extends SendPacket<InputStream> {

    private final InputStream mInputStream;

    public StreamDirectSendPacket(InputStream inputStream) {
        mInputStream = inputStream;
        //长度不固定，则设置为最大值。
        this.mLength = Packet.MAX_PACKET_SIZE;
    }

    @Override
    public byte getType() {
        return Packet.TYPE_STREAM_DIRECT;
    }

    @Override
    protected InputStream createStream() {
        return mInputStream;
    }

}