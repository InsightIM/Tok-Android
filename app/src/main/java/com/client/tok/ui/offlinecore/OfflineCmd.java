package com.client.tok.ui.offlinecore;

public enum OfflineCmd {
    TOX_MESSAGE_OFFLINE_QUERY_FRIEND_REQUEST(0),
    TOX_MESSAGE_OFFLINE_QUERY_FRIEND_RESPONSE(1),
    TOX_MESSAGE_OFFLINE_SEND_REQUEST(2),
    TOX_MESSAGE_OFFLINE_SEND_RESPONSE(3),
    TOX_MESSAGE_OFFLINE_READ_NOTICE(4),
    TOX_MESSAGE_OFFLINE_PULL_REQUEST(5),
    TOX_MESSAGE_OFFLINE_PULL_RESPONSE(6),
    TOX_MESSAGE_OFFLINE_DEL_REQUEST(7),
    TOX_MESSAGE_DEVICE_REQUEST(8);

    private int type;

    OfflineCmd(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static OfflineCmd get(int type) {
        for (OfflineCmd cmd : OfflineCmd.values()) {
            if (type == cmd.getType()) {
                return cmd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "OfflineCmd{" + "type=" + type + '}';
    }
}
