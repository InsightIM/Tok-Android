package com.client.tok.db;

public class DBConstants {
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_STATUS_MESSAGE = "status_message";
    public static final String COLUMN_NICK_NAME = "nick_name";
    public static final String COLUMN_LOGIN_TIME = "login_time";


    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ISONLINE = "isonline";
    public static final String COLUMN_RECEIVED_AVATAR = "received_avatar";
    public static final String COLUMN_ALIAS = "alias";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_HAS_OFFLINE_BOT = "has_offline_bot";

    public static final String TABLE_GROUP_PEERS = "group_peers";

    //group id, group doesn't have pk,only id
    public static final String COLUMN_GROUP_NUMBER = "group_number";
    //group name
    public static final String COLUMN_GROUP_TITLE = "title";
    //contact type，friend or group
    public static final String COLUMN_GROUP_CONTACT_TYPE = "contact_type";
    //group alias
    public static final String COLUMN_GROUP_ALIAS = "alias";
    // TODO Reserve
    public static final String COLUMN_GROUP_STATUS = "status";
    // TODO Reserve
    public static final String COLUMN_GROUP_SIGNATURE = "signature";
    // TODO Reserve
    public static final String COLUMN_GROUP_NOTICE = "notice";
    // TODO Reserve
    public static final String COLUMN_GROUP_AVATAR = "avatar";

    //group peer size
    public static final String COLUMN_GROUP_SIZE = "size";
    public static final String COLUMN_GROUP_PEER_NAME = "peer_name";
    public static final String COLUMN_GROUP_PEER_PK = "peer_pk";
    public static final String COLUMN_GROUP_PEER_SIGNATURE = "peer_signature";

    //sender peer number
    public static final String COLUMN_GROUP_SENDER_PEER_NUMBER = "sender_peer_number";
    public static final String COLUMN_GROUP_TIMESTAMP = "timestamp";
    public static final String COLUMN_GROUP_SENDER_NAME = "sender_name";
    public static final String COLUMN_GROUP_MSG = "msg";
    //message has send success
    public static final String COLUMN_GROUP_HAS_SEND = "has_send";
    //receiver receive the message id
    public static final String COLUMN_GROUP_ACCEPT_ID = "accept_id";
    //message has readed
    public static final String COLUMN_GROUP_HAS_READ = "has_read";
    //message type:text/file/draft/hello
    public static final String COLUMN_GROUP_MSG_TYPE = "msg_type";
    //message size(when message type is file)
    public static final String COLUMN_GROUP_FILE_SIZE = "file_size";
}
