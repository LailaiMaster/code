package clink.impl.single;

import clink.core.IoProvider;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 单线程 Selector
 */
public class SingleSelectorProvider implements IoProvider {

    private final SingleSelectorThread thread;

    public SingleSelectorProvider() throws IOException {
        Selector selector = Selector.open();
        thread = new SingleSelectorThread(selector) {
            @Override
            protected boolean processTask(IoTask task) {
                task.providerCallback.run();
                return false;
            }
        };
        thread.start();
    }

    @Override
    public boolean registerInput(SocketChannel channel, HandleProviderCallback callback) {
        return thread.register(channel, SelectionKey.OP_READ, callback);
    }

    @Override
    public boolean registerOutput(SocketChannel channel, HandleProviderCallback callback) {
        return thread.register(channel, SelectionKey.OP_WRITE, callback);
    }

    @Override
    public void unRegisterInput(SocketChannel channel) {
        thread.unregister(channel);
    }

    @Override
    public void unRegisterOutput(SocketChannel channel) {
        //不需要操作
    }

    @Override
    public void close() {
        thread.exit();
    }

}