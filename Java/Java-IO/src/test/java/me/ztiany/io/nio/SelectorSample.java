package me.ztiany.io.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/9/14 22:59
 */
public class SelectorSample {

    @Test
    public void testCancelledKeyException() throws IOException {
        //server
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.bind(new InetSocketAddress(9898));

        //client
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        sChannel.configureBlocking(false);
        Selector selector = Selector.open();

        SelectionKey selectionKey = sChannel.register(selector, SelectionKey.OP_ACCEPT & SelectionKey.OP_WRITE, "ABC");
        System.out.println(selectionKey.attachment());

        //当 cancel 后，再次 register，会触发 CancelledKeyException。
        selectionKey.cancel();
        selectionKey = sChannel.register(selector, SelectionKey.OP_ACCEPT & SelectionKey.OP_WRITE, "BDF");
        System.out.println(selectionKey.attachment());

        selector.close();
        sChannel.close();
        ssChannel.close();
    }

}
