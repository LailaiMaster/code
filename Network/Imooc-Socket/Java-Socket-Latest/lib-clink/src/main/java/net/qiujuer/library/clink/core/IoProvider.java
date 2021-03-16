package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

public interface IoProvider extends Closeable {
    void register(HandleProviderCallback callback) throws Exception;

    void unregister(SocketChannel channel);

    abstract class HandleProviderCallback extends IoTask implements Runnable {
        private final IoProvider ioProvider;
        /**
         * 附加本次未完全消费完成的IoArgs，然后进行自循环
         */
        protected volatile IoArgs attach;

        protected HandleProviderCallback(IoProvider provider, SocketChannel channel, int ops) {
            super(channel, ops);
            this.ioProvider = provider;
        }

        @Override
        public final void run() {
            final IoArgs attach = this.attach;
            this.attach = null;
            if (onProvideIo(attach)) {
                try {
                    ioProvider.register(this);
                } catch (Exception e) {
                    fireThrowable(e);
                }
            }
        }

        @Override
        public final boolean onProcessIo() {
            final IoArgs attach = this.attach;
            this.attach = null;
            return onProvideIo(attach);
        }

        /**
         * 可以进行接收或者发送时的回调
         *
         * @param args 携带之前的附加值
         */
        protected abstract boolean onProvideIo(IoArgs args);

        /**
         * 检查当前的附加值是否未null，如果处于自循环时当前附加值不为null，
         * 此时如果外层有调度注册异步发送或者接收是错误的
         */
        public void checkAttachNull() {
            if (attach != null) {
                throw new IllegalStateException("Current attach is not empty!");
            }
        }
    }
}
