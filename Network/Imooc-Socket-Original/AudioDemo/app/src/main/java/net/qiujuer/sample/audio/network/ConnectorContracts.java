package net.qiujuer.sample.audio.network;

/**
 * 链接基础口令
 */
public interface ConnectorContracts {
    // 绑定Stream到一个命令链接
    String COMMAND_CONNECTOR_BIND = "--m c bind ";
    // 创建对话房间
    String COMMAND_AUDIO_CREATE_ROOM = "--m a create";
    // 加入对话房间(携带参数)
    String COMMAND_AUDIO_JOIN_ROOM = "--m a join ";
    // 主动离开对话房间
    String COMMAND_AUDIO_LEAVE_ROOM = "--m a leave";

    // 回送服务器上的唯一标志
    String COMMAND_INFO_NAME = "--i server ";
    // AudioInfo信息前缀
    String COMMAND_INFO_AUDIO_PREFIX = "--i a ";
    // 回送语音群名
    String KEY_COMMAND_INFO_AUDIO_ROOM = "room";
    // 回送语音开始
    String KEY_COMMAND_INFO_AUDIO_START = "start";
    // 回送语音结束
    String KEY_COMMAND_INFO_AUDIO_STOP = "stop";
    // 回送语音操作错误
    String KEY_COMMAND_INFO_AUDIO_ERROR = "error";
}
