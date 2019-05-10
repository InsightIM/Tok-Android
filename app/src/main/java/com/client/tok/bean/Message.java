package com.client.tok.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.client.tok.constant.FileKind;
import com.client.tok.constant.MessageType;
import com.client.tok.db.converter.FileKindConverter;
import com.client.tok.db.converter.MessageTypeConverter;
import com.client.tok.db.converter.ToxKeyConverter;
import com.client.tok.tox.ToxManager;

@Entity(tableName = "friend_messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;
    @ColumnInfo(name = "message_id")
    private long messageId;
    @TypeConverters(ToxKeyConverter.class)
    @ColumnInfo(name = "tox_key")
    private ContactsKey key;
    @TypeConverters(ToxKeyConverter.class)
    @ColumnInfo(name = "sender_key")
    private ContactsKey senderKey;
    @ColumnInfo(name = "sender_name")
    private String senderName;
    @ColumnInfo(name = "message")
    private String message;
    /**
     * -1：send fail
     * 0：sending
     * 1：send success
     */
    @ColumnInfo(name = "sent_status")
    private int sentStatus;
    @ColumnInfo(name = "receive_status")
    private int receiveStatus;
    @ColumnInfo(name = "has_been_read")
    private boolean read;
    @ColumnInfo(name = "has_played")
    private boolean hasPlayed;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "size")
    private long size;
    @TypeConverters(MessageTypeConverter.class)
    @ColumnInfo(name = "type")
    private MessageType msgType;
    @TypeConverters(FileKindConverter.class)
    @ColumnInfo(name = "file_kind")
    private FileKind fileKind;

    public Message(int id, long messageId, ContactsKey key, ContactsKey senderKey, String senderName,
        String message, int sentStatus, int receiveStatus, boolean read, boolean hasPlayed,
        long timestamp, long size, MessageType msgType, FileKind fileKind) {
        this.id = id;
        this.messageId = messageId;
        this.key = key;
        this.senderKey = senderKey;
        this.senderName = senderName;
        this.message = message;
        this.sentStatus = sentStatus;
        this.receiveStatus = receiveStatus;
        this.read = read;
        this.hasPlayed = hasPlayed;
        this.timestamp = timestamp;
        this.size = size;
        this.msgType = msgType;
        this.fileKind = fileKind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public ContactsKey getKey() {
        return key;
    }

    public void setKey(ContactsKey key) {
        this.key = key;
    }

    public ContactsKey getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(ContactsKey senderKey) {
        this.senderKey = senderKey;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(int receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isHasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public FileKind getFileKind() {
        return fileKind;
    }

    public void setFileKind(FileKind fileKind) {
        this.fileKind = fileKind;
    }

    public boolean isMine() {
        return senderKey.key.equals(ToxManager.getManager().toxBase.getSelfKey().key);
    }

    public boolean isFileTransfer() {
        return MessageType.transferValues().contains(msgType);
    }

    public int getMsgTypeVal() {
        return msgType.getType();
    }

    @Override
    public String toString() {
        return "Message{"
            + "id="
            + id
            + ", messageId="
            + messageId
            + ", key="
            + key
            + ", senderKey="
            + senderKey
            + ", senderName='"
            + senderName
            + '\''
            + ", message='"
            + message
            + '\''
            + ", sentStatus="
            + sentStatus
            + ", receiveStatus="
            + receiveStatus
            + ", read="
            + read
            + ", hasPlayed="
            + hasPlayed
            + ", timestamp="
            + timestamp
            + ", size="
            + size
            + ", msgType="
            + msgType
            + ", fileKind="
            + fileKind
            + '}';
    }
}
