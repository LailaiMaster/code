package net.qiujuer.sample.audio;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.IoContext;
import net.qiujuer.library.clink.impl.IoSelectorProvider;
import net.qiujuer.library.clink.impl.IoStealingSelectorProvider;
import net.qiujuer.library.clink.impl.SchedulerImpl;
import net.qiujuer.library.clink.utils.CloseUtils;
import net.qiujuer.sample.audio.audio.AudioRecordThread;
import net.qiujuer.sample.audio.audio.AudioTrackThread;
import net.qiujuer.sample.audio.network.ConnectorContracts;
import net.qiujuer.sample.audio.network.ConnectorInfo;
import net.qiujuer.sample.audio.network.ServerNamedConnector;
import net.qiujuer.sample.audio.plugin.BlockingAvailableInputStream;
import net.qiujuer.library.clink.box.StreamDirectSendPacket;
import net.qiujuer.library.clink.utils.plugin.CircularByteBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 业务逻辑处理部分
 */
public class Presenter implements AppContract.Presenter, ServerNamedConnector.ConnectorStatusListener {
    // 音频录制缓冲区，录制的声音将缓存到当前缓冲区等待被网络读取发送
    private final CircularByteBuffer mAudioRecordBuffer = new CircularByteBuffer(128, true);
    // 音频播放缓冲期，网络接收的语音将存储到当前缓冲区等待被播放器读取播放
    private final CircularByteBuffer mAudioTrackBuffer = new CircularByteBuffer(1024, true);

    private final AppContract.View mView;

    // 传输命令的链接
    private volatile ServerNamedConnector mCmdConnector;
    // 传输具体音频的链接
    private volatile ServerNamedConnector mStreamConnector;

    private volatile AudioTrackThread mAudioTrackThread;
    private volatile AudioRecordThread mAudioRecordThread;
    private volatile StreamDirectSendPacket mAudioRecordStreamDirectSendPacket;

    Presenter(AppContract.View view) {
        mView = view;
        mView.showProgressDialog(R.string.dialog_linking);
        Run.onBackground(new InitAction());
    }

    @Override
    public void leaveRoom() {
        if (checkConnector()) {
            mView.showProgressDialog(R.string.dialog_loading);
            mCmdConnector.send(ConnectorContracts.COMMAND_AUDIO_LEAVE_ROOM);
        }
    }

    @Override
    public void joinRoom(String code) {
        if (checkConnector()) {
            mView.showProgressDialog(R.string.dialog_loading);
            mCmdConnector.send(ConnectorContracts.COMMAND_AUDIO_JOIN_ROOM + code);
        }
    }

    @Override
    public void createRoom() {
        if (checkConnector()) {
            mView.showProgressDialog(R.string.dialog_loading);
            mCmdConnector.send(ConnectorContracts.COMMAND_AUDIO_CREATE_ROOM);
        }
    }

    @Override
    public void destroy() {
        stopAudioThread();
        Run.onBackground(new DestroyAction());
    }

    @Override
    public void onConnectorClosed(Connector connector) {
        destroy();
        dismissDialogAndToast(R.string.toast_connector_closed, false);
    }


    /**
     * 检查当前的连接是否可用
     *
     * @return True 可用
     */
    private boolean checkConnector() {
        if (mCmdConnector == null || mStreamConnector == null) {
            mView.showToast(R.string.toast_bad_network);
            return false;
        }
        return true;
    }

    /**
     * 停止Audio
     */
    private void stopAudioThread() {
        if (mAudioRecordStreamDirectSendPacket != null) {
            // 停止发送包
            CloseUtils.close(mAudioRecordStreamDirectSendPacket.open());
            mAudioRecordStreamDirectSendPacket = null;
        }

        if (mAudioRecordThread != null) {
            // 停止录制
            mAudioRecordThread.interrupt();
            mAudioRecordThread = null;
        }

        if (mAudioTrackThread != null) {
            // 停止播放
            mAudioTrackThread.interrupt();
            mAudioTrackThread = null;
        }

        // 清理缓冲区
        mAudioTrackBuffer.clear();
        mAudioRecordBuffer.clear();
    }

    /**
     * 开始Audio
     */
    private void startAudioThread() {
        // 发送直流包
        mAudioRecordStreamDirectSendPacket = new StreamDirectSendPacket(new BlockingAvailableInputStream(mAudioRecordBuffer.getInputStream()));
        mStreamConnector.send(mAudioRecordStreamDirectSendPacket);

        // 启动音频
        AudioRecordThread audioRecordThread = new AudioRecordThread(mAudioRecordBuffer.getOutputStream());
        AudioTrackThread audioTrackThread = new AudioTrackThread(new BlockingAvailableInputStream(mAudioTrackBuffer.getInputStream()),
                audioRecordThread.getAudioRecord().getAudioSessionId());

        audioTrackThread.start();
        audioRecordThread.start();

        mAudioTrackThread = audioTrackThread;
        mAudioRecordThread = audioRecordThread;
    }

    /**
     * 移除弹出框，并且显示Toast
     */
    private void dismissDialogAndToast(final int toast, final boolean online) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                mView.dismissProgressDialog();
                mView.showToast(toast);
                if (online) {
                    mView.onOnline();
                } else {
                    mView.onOffline();
                }
            }
        });
    }

    /**
     * 设置群Code到UI层
     *
     * @param code 群标志
     */
    private void setViewRoomCode(final String code) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                mView.showRoomCode(code);
            }
        });
    }

    private class InitAction implements Action {
        @Override
        public void call() {
            try {
                IoContext.setup()
                        .ioProvider(new IoSelectorProvider())
                        .scheduler(new SchedulerImpl(1))
                        .start();

                mCmdConnector = new ServerNamedConnector(AppContract.SERVER_ADDRESS, AppContract.PORT);
                mCmdConnector.setMessageArrivedListener(mMessageArrivedListener);
                mCmdConnector.setConnectorStatusListener(Presenter.this);

                mStreamConnector = new ServerNamedConnector(AppContract.SERVER_ADDRESS, AppContract.PORT) {
                    @Override
                    protected OutputStream createNewReceiveDirectOutputStream(long l, byte[] bytes) {
                        return mAudioTrackBuffer.getOutputStream();
                    }
                };
                mStreamConnector.setConnectorStatusListener(Presenter.this);

                // 发送绑定命令
                mCmdConnector.send(ConnectorContracts.COMMAND_CONNECTOR_BIND + mStreamConnector.getServerName());

                // 成功
                dismissDialogAndToast(R.string.toast_link_succeed, false);
            } catch (IOException e) {
                // 销毁
                new DestroyAction().call();
                // 失败
                dismissDialogAndToast(R.string.toast_link_failed, false);
            }
        }
    }

    private ServerNamedConnector.MessageArrivedListener mMessageArrivedListener = new ServerNamedConnector.MessageArrivedListener() {
        @Override
        public void onNewMessageArrived(ConnectorInfo info) {
            switch (info.getKey()) {
                case ConnectorContracts.KEY_COMMAND_INFO_AUDIO_ROOM:
                    setViewRoomCode(info.getValue());
                    dismissDialogAndToast(R.string.toast_room_name, true);
                    break;
                case ConnectorContracts.KEY_COMMAND_INFO_AUDIO_START:
                    startAudioThread();
                    dismissDialogAndToast(R.string.toast_start, true);
                    break;
                case ConnectorContracts.KEY_COMMAND_INFO_AUDIO_STOP:
                    stopAudioThread();
                    dismissDialogAndToast(R.string.toast_stop, false);
                    break;
                case ConnectorContracts.KEY_COMMAND_INFO_AUDIO_ERROR:
                    stopAudioThread();
                    dismissDialogAndToast(R.string.toast_error, false);
                    break;
            }
        }
    };

    private class DestroyAction implements Action {
        @Override
        public void call() {
            CloseUtils.close(mCmdConnector, mStreamConnector);
            mCmdConnector = null;
            mStreamConnector = null;
            try {
                IoContext.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
