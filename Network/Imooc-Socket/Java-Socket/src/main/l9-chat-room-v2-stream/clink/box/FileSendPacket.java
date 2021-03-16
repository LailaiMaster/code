package clink.box;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import clink.core.SendPacket;

/**
 * 以文件的形式发送包，因为文件可能很大，不适合一次性读入内存。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/23 0:24
 */
public class FileSendPacket extends SendPacket<FileInputStream> {

    private final File mFile;

    public FileSendPacket(File file) {
        mFile = file;
        this.mLength = mFile.length();
    }

    @Override
    public byte getType() {
        return TYPE_STREAM_FILE;
    }

    @Override
    protected FileInputStream createStream() {
        try {
            return new FileInputStream(mFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
