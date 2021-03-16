package server;

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

import clink.box.StringReceivePacket;
import clink.core.Connector;
import clink.core.ScheduleJob;
import clink.core.schedule.IdleTimeoutScheduleJob;
import clink.utils.CloseUtils;
import foo.Foo;
import foo.handler.ConnectorCloseChain;
import foo.handler.ConnectorHandler;
import foo.handler.ConnectorStringPacketChain;
import server.audio.AudioRoom;


/**
 * TCP 服务器，用于获取客户端连接，创建 ClientHandler 来处理客户端连接。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/1 21:17
 */
class TCPServer {

    private final int portServer;

    /**
     * 所有建立的连接
     */
    private final List<ConnectorHandler> connectorHandlerList = new ArrayList<>();

    /**
     * 文件缓存路径
     */
    private final File cachePath;

    /**
     * 用于接受客户端连接
     */
    private ServerAcceptor acceptor;

    private ServerSocketChannel serverSocketChannel;

    /**
     * 所有的群
     */
    private final Map<String, Group> groups = new HashMap<>();

    /**
     * 新连接建立监听与处理
     */
    private final ServerAcceptor.AcceptListener mAcceptListener = new ServerAcceptor.AcceptListener() {

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

                //客户端和服务器，谁的超时时间短谁就能发送心跳
                ScheduleJob scheduleJob = new IdleTimeoutScheduleJob(10, TimeUnit.SECONDS, connectorHandler);
                connectorHandler.schedule(scheduleJob);

                //添加到连接管理中
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
    };

    TCPServer(int portServer, File cachePath) {
        this.portServer = portServer;
        this.cachePath = cachePath;
        //创建一个群
        this.groups.put(Foo.DEFAULT_GROUP_NAME, new Group(Foo.DEFAULT_GROUP_NAME, mGroupMessageAdapter));
    }

    ///////////////////////////////////////////////////////////////////////////
    // 启动与关闭
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 启动服务器
     */
    boolean start() {
        try {
            ServerAcceptor clientListener = new ServerAcceptor(mAcceptListener);

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//配置非阻塞
            serverSocketChannel.bind(new InetSocketAddress(portServer));
            serverSocketChannel.register(clientListener.getSelector(), SelectionKey.OP_ACCEPT);

            System.out.println("服务器信息：" + serverSocketChannel.getLocalAddress());

            clientListener.start();
            acceptor = clientListener;

            if (acceptor.awaitRunning()) {
                System.out.println("服务器准备就绪～");
                System.out.println("服务器信息：" + serverSocketChannel.getLocalAddress().toString());
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
     * 停止服务器
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

        CloseUtils.close(serverSocketChannel);
    }

    /**
     * 用于处理连接关闭
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

    ///////////////////////////////////////////////////////////////////////////
    // 信息统计
    ///////////////////////////////////////////////////////////////////////////

    private final ServerStatistics statistics = new ServerStatistics();

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

    ///////////////////////////////////////////////////////////////////////////
    // 消费的发送
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 给所有客户端发送消息
     */
    void broadcast(String line) {
        line = "系统通知：" + line;
        ConnectorHandler[] connectorHandlers;
        synchronized (connectorHandlerList) {
            connectorHandlers = connectorHandlerList.toArray(new ConnectorHandler[0]);
        }
        for (ConnectorHandler connectorHandler : connectorHandlers) {
            sendMessageToClient(connectorHandler, line);
        }
    }

    /**
     * 发送消息给某个客户端
     *
     * @param handler 客户端
     * @param msg     消息
     */
    public void sendMessageToClient(ConnectorHandler handler, String msg) {
        handler.send(msg);
        statistics.sendSize++;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 群聊支持
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 处理群消息发送的 Adapter
     */
    private final Group.GroupMessageAdapter mGroupMessageAdapter = TCPServer.this::sendMessageToClient;

    /**
     * 用于处理通过 String 发送的命令，如果不是命令，则返回给客户端。
     */
    private class ParseCommandConnectorStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String entity = stringReceivePacket.getEntity();
            switch (entity) {
                case Foo.COMMAND_GROUP_JOIN: {
                    Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
                    if (group.addMember(handler)) {
                        sendMessageToClient(handler, "Join Group:" + group.getName());
                    }
                    return true;
                }
                case Foo.COMMAND_GROUP_LEAVE: {
                    Group group = groups.get(Foo.DEFAULT_GROUP_NAME);
                    if (group.removeMember(handler)) {
                        sendMessageToClient(handler, "Leave Group:" + group.getName());
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean consumeAgain(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            // 捡漏的模式，当我们第一遍未消费，然后又没有加入到群，自然没有后续的节点消费，此时我们进行二次消费，返回发送过来的消息
            mGroupMessageAdapter.sendMessageToClient(handler, "server replay：" + stringReceivePacket.getEntity());
            return true;
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // 一对一桥接通信支持
    ///////////////////////////////////////////////////////////////////////////

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

    /**
     * 音频命令控制链接映射表
     */
    private final HashMap<ConnectorHandler, ConnectorHandler> audioCmdToStreamMap = new HashMap<>(100);

    /**
     * 数据流传输链接映射表
     */
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

    /**
     * 房间映射表, 房间号-房间的映射
     */
    private final HashMap<String, AudioRoom> audioRoomMap = new HashMap<>(50);

    /**
     * 链接与房间的映射表，音频链接-房间的映射
     */
    private final HashMap<ConnectorHandler, AudioRoom> audioStreamRoomMap = new HashMap<>(100);

    /**
     * 音频命令解析
     */
    private class ParseAudioStreamCommandStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            String str = stringReceivePacket.getEntity();
            if (str.startsWith(Foo.COMMAND_CONNECTOR_BIND)) {
                // 绑定命令，也就是将音频流绑定到当前的命令流上【一个客户端会有两个连接，一个用于传输命令，一个用于传输流。】
                String key = str.substring(Foo.COMMAND_CONNECTOR_BIND.length());
                ConnectorHandler audioStreamConnector = findConnectorFromKey(key);
                if (audioStreamConnector != null) {
                    // 添加绑定关系【双向绑定】
                    audioCmdToStreamMap.put(handler, audioStreamConnector);
                    audioStreamToCmdMap.put(audioStreamConnector, handler);
                    // 转换为桥接模式
                    audioStreamConnector.changeToBridge();
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

                        // 相互搭建好乔
                        theOtherHandler.bindToBridge(audioStreamConnector.getSender());
                        audioStreamConnector.bindToBridge(theOtherHandler.getSender());

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
            // 解除侨界
            connector.unBindToBridge();
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