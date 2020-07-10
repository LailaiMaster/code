package net.qiujuer.sample.audio.plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * 阻塞可读性InputStream，当不可读时将阻塞到有数据可读
 */
public class BlockingAvailableInputStream extends InputStream {
    private final InputStream inputStream;

    public BlockingAvailableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int available() throws IOException {
        do {
            int available = inputStream.available();
            if (available == 0) {
                // 可读数据为0时, 放弃当前的循环机会，等待下一次CPU调度
                Thread.yield();
            } else {
                // 返回有数据可读，或-1停止
                return available;
            }
        } while (true);
    }
}
