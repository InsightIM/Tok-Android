package com.client.tok.bean;

import android.arch.persistence.room.Entity;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;

@Entity
public class ConversationItem {
    public String cKey;
    public String name;
    public String alias;
    public int contactType;
    public boolean isOnline;
    public String status;
    public boolean isMute;
    public int msgType;
    public long lastMsgDbId;
    public String lastMsg;
    public long lastMsgTime;
    //note,this is a tag ,not exactly, don't use it as the unread msg size
    public int unreadCount;
    public long updateTime;

    public String getcKey() {
        return cKey;
    }

    public void setcKey(String cKey) {
        this.cKey = cKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastMsgDbId() {
        return lastMsgDbId;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public void setLastMsgDbId(long lastMsgDbId) {
        this.lastMsgDbId = lastMsgDbId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getDisplayName() {
        if (!StringUtils.isEmpty(alias)) {
            return alias;
        } else if (!StringUtils.isEmpty(name)) {
            return name;
        } else {
            return PkUtils.simplePk(cKey);
        }
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ConversationItem{"
            + "cKey='"
            + cKey
            + '\''
            + ", name='"
            + name
            + '\''
            + ", alias='"
            + alias
            + '\''
            + ", contactType="
            + contactType
            + ", isOnline="
            + isOnline
            + ", status='"
            + status
            + '\''
            + ", isMute="
            + isMute
            + ", msgType="
            + msgType
            + ", lastMsgDbId="
            + lastMsgDbId
            + ", lastMsg='"
            + lastMsg
            + '\''
            + ", lastMsgTime="
            + lastMsgTime
            + ", unreadCount="
            + unreadCount
            + ", updateTime="
            + updateTime
            + '}';
    }
}
