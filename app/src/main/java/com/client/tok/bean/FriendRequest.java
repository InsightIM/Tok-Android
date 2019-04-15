package com.client.tok.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.client.tok.db.converter.ToxKeyConverter;
import java.io.Serializable;

@Entity(tableName = "friend_requests")
public class FriendRequest implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;
    @TypeConverters(ToxKeyConverter.class)
    @ColumnInfo(name = "tox_key")
    private ContactsKey requestKey;
    @ColumnInfo(name = "message")
    private String requestMessage;
    @ColumnInfo(name = "has_read")
    private boolean hasRead;

    public FriendRequest() {

    }

    @Ignore
    public FriendRequest(ContactsKey requestKey, String requestMessage) {
        this.requestKey = requestKey;
        this.requestMessage = requestMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return requestKey != null ? requestKey.getKey() : "";
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    public ContactsKey getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(ContactsKey requestKey) {
        this.requestKey = requestKey;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }
}
