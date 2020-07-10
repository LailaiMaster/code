package net.qiujuer.sample.audio.network;

/**
 * 基础返回信息Model
 */
public class ConnectorInfo {
    private final String key;
    private final String value;

    ConnectorInfo(String msg) {
        String[] strings = msg.split(" ");
        key = strings[0];
        if (strings.length == 2) {
            value = strings[1];
        } else {
            value = null;
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
