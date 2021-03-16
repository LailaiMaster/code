package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import client.ServerInfo;
import client.TCPClient;
import client.UDPSearcher;
import clink.core.Connector;
import clink.core.IoContext;
import clink.impl.stealing.IoStealingSelectorProvider;
import clink.impl.SchedulerImpl;
import foo.Foo;
import foo.handler.ConnectorCloseChain;
import foo.handler.ConnectorHandler;

/**
 * 压力测试
 */
public class PressureTester {

    /*
    不考虑发送消耗，并发量：
        2000*4/400*1000 = 2w/s 算上来回 2 次数据解析：4w/s
        2000 个客户端 4 个线程每 400 毫秒发送一次数据。
     */
    private static final int CLIENT_SIZE = 2000;
    private static final int SEND_THREAD_SIZE = 4;
    private static final int SEND_THREAD_DELAY = 400;
    private static volatile boolean done;

    public static void main(String[] args) throws IOException {
        ServerInfo info = UDPSearcher.searchServer(10000);
        System.out.println("Server:" + info);
        if (info == null) {
            return;
        }

        File cachePath = Foo.getCacheDir("client/test");
        IoContext.setup()
                .ioProvider(new IoStealingSelectorProvider(1))
                //TODO：性能优化2（单线程selector）
                //.ioProvider(new SingleSelectorProvider())
                //TODO：性能优化3（多线程任务窃取）
                .ioProvider(new IoStealingSelectorProvider(3))
                .scheduler(new SchedulerImpl(1))
                .start();

        // 当前连接数量
        int size = 0;
        final List<TCPClient> tcpClients = new ArrayList<>(CLIENT_SIZE);

        // 关闭时移除
        final ConnectorCloseChain closeChain = new ConnectorCloseChain() {
            @Override
            protected boolean consume(ConnectorHandler handler, Connector connector) {
                System.err.println(connector.getKey() + " exit automatically");
                return false;
            }
        };

        // 添加
        for (int i = 0; i < CLIENT_SIZE; i++) {
            try {
                TCPClient tcpClient = TCPClient.linkWith(info, cachePath, false);
                if (tcpClient == null) {
                    throw new NullPointerException();
                }
                // 添加关闭链式节点
                tcpClient.getCloseChain().appendLast(closeChain);
                tcpClients.add(tcpClient);
                System.out.println("连接成功：" + (++size));
            } catch (NullPointerException e) {
                System.out.println("连接异常");
                break;
            }
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        bufferedReader.readLine();

        Runnable runnable = () -> {
            while (!done) {
                TCPClient[] copyClients = tcpClients.toArray(new TCPClient[0]);
                for (TCPClient client : copyClients) {
                    client.send("Hello~~");
                }

                if (SEND_THREAD_DELAY > 0) {
                    try {
                        Thread.sleep(SEND_THREAD_DELAY);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        };

        List<Thread> threads = new ArrayList<>(SEND_THREAD_SIZE);

        for (int i = 0; i < SEND_THREAD_SIZE; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }

        bufferedReader.readLine();

        // 等待线程完成
        done = true;

        // 客户端结束操作
        for (TCPClient tcpClient : tcpClients) {
            tcpClient.exit();
        }

        // 关闭框架线程池
        IoContext.close();

        // 强制结束处于等待的线程
        for (Thread thread : threads) {
            try {
                thread.interrupt();
            } catch (Exception ignored) {
            }
        }
    }

}