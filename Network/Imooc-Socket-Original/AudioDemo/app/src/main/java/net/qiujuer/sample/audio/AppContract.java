package net.qiujuer.sample.audio;

/**
 * 公共契约
 */
public interface AppContract {
    // 服务器地址
    String SERVER_ADDRESS = "192.168.124.3";
    // 服务器端口
    int PORT = 30401;

    interface View {
        void showProgressDialog(int string);

        void dismissProgressDialog();

        void showToast(int string);

        /**
         * 显示房间号
         *
         * @param code 房间号
         */
        void showRoomCode(String code);

        /**
         * 在线
         */
        void onOnline();

        /**
         * 离线
         */
        void onOffline();
    }

    interface Presenter {
        /**
         * 主动离开房间
         */
        void leaveRoom();

        /**
         * 加入已有房间
         *
         * @param code 房间号
         */
        void joinRoom(String code);

        /**
         * 创建房间
         */
        void createRoom();

        /**
         * 退出APP操作
         */
        void destroy();
    }
}
