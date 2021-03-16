package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.Foo;
import net.qiujuer.lesson.sample.foo.handle.ConnectorCloseChain;
import net.qiujuer.lesson.sample.foo.handle.ConnectorHandler;
import net.qiujuer.lesson.sample.foo.handle.ConnectorStringPacketChain;
import net.qiujuer.lesson.sample.server.audio.AudioRoom;
import net.qiujuer.library.clink.box.StringReceivePacket;
import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.ScheduleJob;
import net.qiujuer.library.clink.core.schedule.IdleTimeoutScheduleJob;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TCPServer implements ServerAcceptor.AcceptListener,
        Group.GroupMessageAdapter {
    private final int port;
    private final File cachePath;
    private final List<ConnectorHandler> connectorHandlerList = new ArrayList<>();
    private final Map<String, Group> groups = new HashMap<>();
    private ServerAcceptor acceptor;
    private ServerSocketChannel server;

    private final ServerStatistics statistics = new ServerStatistics();

    TCPServer(int port, File cachePath) {
        this.port = port;
        this.cachePath = cachePath;
        this.groups.put(Foo.DEFAULT_GROUP_NAME, new Group(Foo.DEFAULT_GROUP_NAME, this));
    }

    boolean start() {
        try {
            // 启动Acceptor线程
            ServerAcceptor acceptor = new ServerAcceptor(this);

            ServerSocketChannel server = ServerSocketChannel.open();
            // 设置为非阻塞
            server.configureBlocking(false);
            // 绑定本地端口
            server.socket().bind(new InetSocketAddress(port));
            // 注册客户端连接到达监听
            server.register(acceptor.getSelector(), SelectionKey.OP_ACCEPT);

            this.server = server;
            this.acceptor = acceptor;

            // 线程需要启动
            acceptor.start();
            if (acceptor.awaitRunning()) {
                System.out.println("服务器准备就绪～");
                System.out.println("服务器信息：" + server.getLocalAddress().toString());
                return true;
            } else {
                System.out.println("启动异常！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭操作
     */
    void stop() {
        if (acceptor != null) {
            acceptor.exit();
        }

        ConnectorHandler[] connectorHandlers;
        synchronized (connectorHandlerList) {
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
            connectorHandlerList.clear();
        }
        for (ConnectorHandler connectorHandler : connectorHandlers) {
            connectorHandler.exit();
        }

        CloseUtils.close(server);
    }

    /**
     * 进行广播发送，发给所有客户端
     *
     * @param str 消息
     */
    void broadcast(String str) {
        str = "系统通知：" + str;
        ConnectorHandler[] connectorHandlers;
        synchronized (connectorHandlerList) {
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
        }
        for (ConnectorHandler connectorHandler : connectorHandlers) {
            sendMessageToClient(connectorHandler, str);
        }
    }

    /**
     * 发送消息给某个客户端
     *
     * @param handler 客户端
     * @param msg     消息
     */
    @Override
    public void sendMessageToClient(ConnectorHandler handler, String msg) {
        handler.send(msg);
        statistics.sendSize++;
    }

    /**
     * 获取当前的状态信息
     */
    Object[] getStatusString() {
        return new String[]{
                "客户端数量：" + connectorHandlerList.size(),
                "发送数量：" + statistics.sendSize,
                "接收数量：" + statistics.receiveSize
        };
    }

    /**
     * 新客户端链接时回调
     *
     * @param channel 新客户端
     */
    @Override
    public void onNewSocketArrived(SocketChannel channel) {
        try {
            ConnectorHandler connectorHandler = new ConnectorHandler(channel, cachePath);
            System.out.println(connectorHandler.getClientInfo() + ":Connected!");

            // 添加收到消息的处理责任链
            connectorHandler.getStringPacketChain()
                    .appendLast(statistics.statisticsChain())
                    .appendLast(new ParseCommandConnectorStringPacketChain())
                    .appendLast(new ParseAudioStreamCommandStringPacketChain());

            // 添加关闭链接时的责任链
            connectorHandler.getCloseChain()
                    .appendLast(new RemoveAudioQueueOnConnectorClosedChain())
                    .appendLast(new RemoveQueueOnConnectorClosedChain());

            // 空闲任务调度
            ScheduleJob scheduleJob = new IdleTimeoutScheduleJob(20, TimeUnit.SECONDS, connectorHandler);
            connectorHandler.schedule(scheduleJob);

            synchronized (connectorHandlerList) {
                connectorHandlerList.add(connectorHandler);
                System.out.println("当前客户端数量：" + connectorHandlerList.size());
            }

            // 回送客户端在服务器的唯一标志
            sendMessageToClient(connectorHandler, Foo.COMMAND_INFO_NAME + connectorHandler.getKey().toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端链接异常：" + e.getMessage());
        }
    }


    /**
     * 移除队列，在链接关闭回调时
     */
    private class RemoveQueueOnConnectorClosedChain extends ConnectorCloseChain {

        @Override
        protected boolean consume(ConnectorHandler handler, Connector connector) {
            synchronized (connectorHandlerList) {
                connectorHandlerList.remove(handler);
            }
            // 移除群聊的客户端
            Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
            group.removeMember(handler);
            return true;
        }
    }


    /**
     * 解析收到的消息，当前节点主要做命令的解析，
     * 如果子节点也未进行数据消费，那么则进行二次消费，直接返回收到的数据
     */
    private class ParseCommandConnectorStringPacketChain extends ConnectorStringPacketChain {
        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.entity();
            if (str.startsWith(Foo.COMMAND_GROUP_JOIN)) {
                Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
                if (group.addMember(handler)) {
                    sendMessageToClient(handler, "Join Group:" + group.getName());
                }
                return true;
            } else if (str.startsWith(Foo.COMMAND_GROUP_LEAVE)) {
                Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
                if (group.removeMember(handler)) {
                    sendMessageToClient(handler, "Leave Group:" + group.getName());
                }
                return true;
            }
            return false;
        }

        @Override
        protected boolean consumeAgain(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            // 捡漏的模式，当我们第一遍未消费，然后又没有加入到群，自然没有后续的节点消费
            // 此时我们进行二次消费，返回发送过来的消息
            sendMessageToClient(handler, stringReceivePacket.entity());
            return true;
        }
    }


    /**
     * 从全部列表中通过Key查询到一个链接
     */
    private ConnectorHandler findConnectorFromKey(String key) {
        synchronized (connectorHandlerList) {
            for (ConnectorHandler connectorHandler : connectorHandlerList) {
                if (connectorHandler.getKey().toString().equalsIgnoreCase(key)) {
                    return connectorHandler;
                }
            }
        }
        return null;
    }

    // 音频命令控制与数据流传输链接映射表
    private final HashMap<ConnectorHandler, ConnectorHandler> audioCmdToStreamMap = new HashMap<>(100);
    private final HashMap<ConnectorHandler, ConnectorHandler> audioStreamToCmdMap = new HashMap<>(100);


    /**
     * 通过音频命令控制链接寻找数据传输流链接, 未找到则发送错误
     */
    private ConnectorHandler findAudioStreamConnector(ConnectorHandler handler) {
        ConnectorHandler connectorHandler = audioCmdToStreamMap.get(handler);
        if (connectorHandler == null) {
            sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ERROR);
            return null;
        } else {
            return connectorHandler;
        }
    }

    /**
     * 通过音频数据传输流链接寻找命令控制链接
     */
    private ConnectorHandler findAudioCmdConnector(ConnectorHandler handler) {
        return audioStreamToCmdMap.get(handler);
    }

    // 房间映射表, 房间号-房间的映射
    private final HashMap<String, AudioRoom> audioRoomMap = new HashMap<>(50);
    // 链接与房间的映射表，音频链接-房间的映射
    private final HashMap<ConnectorHandler, AudioRoom> audioStreamRoomMap = new HashMap<>(100);

    /**
     * 音频命令解析
     */
    private class ParseAudioStreamCommandStringPacketChain extends ConnectorStringPacketChain {
        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.entity();
            if (str.startsWith(Foo.COMMAND_CONNECTOR_BIND)) {
                // 绑定命令，也就是将音频流绑定到当前的命令流上
                String key = str.substring(Foo.COMMAND_CONNECTOR_BIND.length());
                ConnectorHandler audioStreamConnector = findConnectorFromKey(key);
                if (audioStreamConnector != null) {
                    // 添加绑定关系
                    audioCmdToStreamMap.put(handler, audioStreamConnector);
                    audioStreamToCmdMap.put(audioStreamConnector, handler);
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_CREATE_ROOM)) {
                // 创建房间操作
                ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
                if (audioStreamConnector != null) {
                    // 随机创建房间
                    AudioRoom room = createNewRoom();
                    // 加入一个客户端
                    joinRoom(room, audioStreamConnector);
                    // 发送成功消息
                    sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ROOM + room.getRoomCode());
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_LEAVE_ROOM)) {
                // 离开房间命令
                ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
                if (audioStreamConnector != null) {
                    // 任意一人离开都销毁房间
                    dissolveRoom(audioStreamConnector);
                    // 发送离开消息
                    sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_STOP);
                }
            } else if (str.startsWith(Foo.COMMAND_AUDIO_JOIN_ROOM)) {
                // 加入房间操作
                ConnectorHandler audioStreamConnector = findAudioStreamConnector(handler);
                if (audioStreamConnector != null) {
                    // 取得房间号
                    String roomCode = str.substring(Foo.COMMAND_AUDIO_JOIN_ROOM.length());
                    AudioRoom room = audioRoomMap.get(roomCode);
                    // 如果找到了房间就走后面流程
                    if (room != null && joinRoom(room, audioStreamConnector)) {
                        // 对方
                        ConnectorHandler theOtherHandler = room.getTheOtherHandler(audioStreamConnector);

                        // 相互搭建好桥
                        Connector.bridge(audioStreamConnector, theOtherHandler);

                        // 成功加入房间
                        sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_START);
                        // 给对方发送可开始聊天的消息
                        sendStreamConnectorMessage(theOtherHandler, Foo.COMMAND_INFO_AUDIO_START);
                    } else {
                        // 房间没找到，房间人员已满
                        sendMessageToClient(handler, Foo.COMMAND_INFO_AUDIO_ERROR);
                    }
                }
            } else {
                return false;
            }
            return true;
        }
    }

    /**
     * 链接关闭时退出音频房间等操作
     */
    private class RemoveAudioQueueOnConnectorClosedChain extends ConnectorCloseChain {

        @Override
        protected boolean consume(ConnectorHandler handler, Connector connector) {
            if (audioCmdToStreamMap.containsKey(handler)) {
                // 命令链接断开
                audioCmdToStreamMap.remove(handler);
            } else if (audioStreamToCmdMap.containsKey(handler)) {
                // 流断开
                audioStreamToCmdMap.remove(handler);
                // 解散房间
                dissolveRoom(handler);
            }
            return false;
        }
    }

    /**
     * 给链接流对应的命令控制链接发送信息
     */
    private void sendStreamConnectorMessage(ConnectorHandler streamConnector, String msg) {
        if (streamConnector != null) {
            ConnectorHandler audioCmdConnector = findAudioCmdConnector(streamConnector);
            sendMessageToClient(audioCmdConnector, msg);
        }
    }

    /**
     * 生成一个当前缓存列表中没有的房间
     */
    private AudioRoom createNewRoom() {
        AudioRoom room;
        do {
            room = new AudioRoom();
        } while (audioRoomMap.containsKey(room.getRoomCode()));
        // 添加到缓存列表
        audioRoomMap.put(room.getRoomCode(), room);
        return room;
    }

    /**
     * 加入房间
     *
     * @return 是否加入成功
     */
    private boolean joinRoom(AudioRoom room, ConnectorHandler streamConnector) {
        if (room.enterRoom(streamConnector)) {
            audioStreamRoomMap.put(streamConnector, room);
            return true;
        }
        return false;
    }

    /**
     * 解散房间
     *
     * @param streamConnector 解散者
     */
    private void dissolveRoom(ConnectorHandler streamConnector) {
        AudioRoom room = audioStreamRoomMap.get(streamConnector);
        if (room == null) {
            return;
        }

        ConnectorHandler[] connectors = room.getConnectors();
        for (ConnectorHandler connector : connectors) {
            // 解除桥接
            connector.relieveBridge();
            // 移除缓存
            audioStreamRoomMap.remove(connector);
            if (connector != streamConnector) {
                // 退出房间 并 获取对方
                sendStreamConnectorMessage(connector, Foo.COMMAND_INFO_AUDIO_STOP);
            }
        }

        // 销毁房间
        audioRoomMap.remove(room.getRoomCode());
    }
}
