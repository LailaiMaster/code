package me.ztiany.io.nio2;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/4 17:21
 */
public class AIOSample {

    @Test
    public void asyncWrite1() throws IOException, InterruptedException, ExecutionException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                Paths.get("jars/test02.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE
        );

        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024 * 1024);
        Future<Integer> result = channel.write(buffer, 0);
        //其他操作
        Integer len = result.get();
        System.out.println(Thread.currentThread() + " len = " + len);
    }

    @Test
    public void asyncWrite2() throws IOException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                Paths.get("jars/test03.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE
        );

        Thread main = Thread.currentThread();

        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024 * 1024);
        channel.write(buffer, 0, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("isMainThread = " + (main == Thread.currentThread()) + " result = " + result + ", attachment = " + attachment);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
            }
        });

        System.out.println("end");
    }

    @Test
    public void startAsyncSimpleServer() throws IOException {
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(10080));
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                //使用clientChannel
                serverChannel.accept(null, this);
            }

            public void failed(Throwable throwable, Void attachment) {
                //错误处理
            }
        });
    }

}