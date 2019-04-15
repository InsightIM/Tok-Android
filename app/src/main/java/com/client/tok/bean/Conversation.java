package com.client.tok.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "friend_conversation")
public class Conversation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;
    @ColumnInfo(name = "tox_key")
    private String key;
    @ColumnInfo(name = "last_msg_db_id")
    private long lastMsgId;
    @ColumnInfo(name = "update_time")
    private long updateTime;
    //note:tag,not exactly, don't use it as the unread msg size
    @ColumnInfo(name = "unread_count")
    private long unreadCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "Conversation{"
            + "id="
            + id
            + ", key='"
            + key
            + '\''
            + ", lastMsgId="
            + lastMsgId
            + ", updateTime="
            + updateTime
            + ", unreadCount="
            + unreadCount
            + '}';
    }
}
