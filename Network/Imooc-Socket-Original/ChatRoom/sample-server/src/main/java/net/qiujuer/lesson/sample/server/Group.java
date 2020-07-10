package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.handle.ConnectorHandler;
import net.qiujuer.lesson.sample.foo.handle.ConnectorStringPacketChain;
import net.qiujuer.library.clink.box.StringReceivePacket;

import java.util.ArrayList;
import java.util.List;

class Group {
    private final String name;
    private final GroupMessageAdapter adapter;
    private final List<ConnectorHandler> members = new ArrayList<>();

    Group(String name, GroupMessageAdapter adapter) {
        this.name = name;
        this.adapter = adapter;
    }

    String getName() {
        return name;
    }

    /**
     * 添加某个客户端
     *
     * @param handler 客户端
     * @return 是否成功
     */
    boolean addMember(ConnectorHandler handler) {
        synchronized (members) {
            if (!members.contains(handler)) {
                members.add(handler);
                handler.getStringPacketChain()
                        .appendLast(new ForwardConnectorStringPacketChain());
                System.out.println("Group[" + name + "] add new member:" + handler.getClientInfo());
                return true;
            }
        }
        return false;
    }

    /**
     * 移除某个客户端
     *
     * @param handler 客户端
     * @return 是否移除成功
     */
    boolean removeMember(ConnectorHandler handler) {
        synchronized (members) {
            if (members.remove(handler)) {
                handler.getStringPacketChain()
                        .remove(ForwardConnectorStringPacketChain.class);
                System.out.println("Group[" + name + "] leave member:" + handler.getClientInfo());
                return true;
            }
        }
        return false;
    }


    /**
     * 进行消息转发的责任链节点
     */
    private class ForwardConnectorStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            synchronized (members) {
                for (ConnectorHandler member : members) {
                    if (member == handler) {
                        continue;
                    }
                    adapter.sendMessageToClient(member, stringReceivePacket.entity());
                }
                return true;
            }
        }
    }

    /**
     * 进行消息发送的Adapter
     */
    interface GroupMessageAdapter {
        /**
         * 发送消息的接口
         *
         * @param handler 客户端
         * @param msg     消息
         */
        void sendMessageToClient(ConnectorHandler handler, String msg);
    }
}
