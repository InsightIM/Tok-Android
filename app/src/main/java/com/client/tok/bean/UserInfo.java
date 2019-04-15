package com.client.tok.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.client.tok.db.converter.ToxMeNameConverter;
import com.client.tok.db.converter.ToxNicknameConverter;
import com.client.tok.db.converter.ToxStatusMessageConverter;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxStatusMessage;

@Entity(tableName = "users")
public class UserInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @TypeConverters(ToxMeNameConverter.class)
    @ColumnInfo(name = "username")
    private ToxMeName toxMeName;
    @ColumnInfo(name = "password")
    private String password;
    @TypeConverters(ToxNicknameConverter.class)
    @ColumnInfo(name = "nickname")
    private ToxNickname nickname;
    @ColumnInfo(name = "status")
    private String status;
    @TypeConverters(ToxStatusMessageConverter.class)
    @ColumnInfo(name = "status_message")
    private ToxStatusMessage statusMessage;
    @ColumnInfo(name = "avatar")
    private String avatarName;
    @ColumnInfo(name = "login_time")
    private long loginTime;

    public UserInfo(ToxMeName toxMeName, String password, ToxNickname nickname, String status,
        ToxStatusMessage statusMessage, String avatarName, long loginTime) {
        this.toxMeName = toxMeName;
        this.password = password;
        this.nickname = nickname;
        this.status = status;
        this.statusMessage = statusMessage;
        this.avatarName = avatarName;
        this.loginTime = loginTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ToxMeName getToxMeName() {
        return toxMeName;
    }

    public void setToxMeName(ToxMeName toxMeName) {
        this.toxMeName = toxMeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ToxNickname getNickname() {
        return nickname;
    }

    public void setNickname(ToxNickname nickname) {
        this.nickname = nickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ToxStatusMessage getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(ToxStatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getProfileName() {
        return toxMeName.getUserName();
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }
}
