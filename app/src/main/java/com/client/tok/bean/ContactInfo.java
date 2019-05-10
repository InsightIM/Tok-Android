package com.client.tok.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.client.tok.db.converter.ToxKeyConverter;
import com.client.tok.db.converter.ToxNicknameConverter;
import com.client.tok.msg.UserStatus;
import com.client.tok.utils.CharacterParserUtil;
import com.client.tok.utils.PkUtils;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxUserStatus;
import java.io.Serializable;

@Entity(tableName = "friend_contacts")
public class ContactInfo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;
    @TypeConverters(ToxKeyConverter.class)
    @ColumnInfo(name = "tox_key")
    protected ContactsKey key;
    @TypeConverters(ToxNicknameConverter.class)
    @ColumnInfo(name = "name")
    protected ToxNickname name;
    @TypeConverters(ToxNicknameConverter.class)
    @ColumnInfo(name = "avatar")
    protected String avatar;
    @ColumnInfo(name = "isonline")
    protected boolean online;
    @ColumnInfo(name = "status")
    protected String status;

    /**
     * my signature or group notice
     */
    @ColumnInfo(name = "note")
    protected String signature;
    @ColumnInfo(name = "received_avatar")
    protected boolean receivedAvatar;
    @ColumnInfo(name = "isblocked")
    protected boolean blocked;
    @ColumnInfo(name = "mute")
    protected boolean mute;
    @TypeConverters(ToxNicknameConverter.class)
    @ColumnInfo(name = "alias")
    protected ToxNickname alias;
    @ColumnInfo(name = "contact_type")
    protected int contactType;
    @ColumnInfo(name = "is_bot")
    protected boolean isBot;
    @ColumnInfo(name = "bot_type")
    protected int botType;
    @ColumnInfo(name = "has_offline_bot")
    protected boolean hasOfflineBot;
    @Ignore
    protected String tokId;
    @Ignore
    protected String provider;
    @Ignore
    protected int defaultIcon;

    public ContactInfo() {

    }

    @Ignore
    public ContactInfo(ContactsKey key, String name, String alias, String signature,
        int contactType) {
        this.key = key;
        this.online = false;
        if (name != null) {
            this.name = ToxNickname.unsafeFromValue(name.getBytes());
        }
        if (alias != null) {
            this.alias = ToxNickname.unsafeFromValue(alias.getBytes());
        }
        this.signature = signature;
        this.blocked = false;
        this.mute = false;
        this.contactType = contactType;
    }

    @Ignore
    public ContactInfo(ContactsKey key, boolean online, ToxNickname name, ToxNickname alias,
        String status, String signature, boolean blocked, boolean mute, int contactType) {
        this.key = key;
        this.online = online;
        this.name = name;
        this.alias = alias;
        this.status = status;
        this.signature = signature;
        this.blocked = blocked;
        this.mute = mute;
        this.contactType = contactType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContactsKey getKey() {
        return key;
    }

    public void setKey(ContactsKey key) {
        this.key = key;
    }

    public ToxNickname getName() {
        return name;
    }

    public void setName(ToxNickname name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isReceivedAvatar() {
        return receivedAvatar;
    }

    public void setReceivedAvatar(boolean receivedAvatar) {
        this.receivedAvatar = receivedAvatar;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public ToxNickname getAlias() {
        return alias;
    }

    public void setAlias(ToxNickname alias) {
        this.alias = alias;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public int getBotType() {
        return botType;
    }

    public void setBotType(int botType) {
        this.botType = botType;
    }

    public boolean isHasOfflineBot() {
        return hasOfflineBot;
    }

    public void setHasOfflineBot(boolean hasOfflineBot) {
        this.hasOfflineBot = hasOfflineBot;
    }

    public String getTokId() {
        return tokId;
    }

    public void setTokId(String tokId) {
        this.tokId = tokId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(String iconName) {
        defaultIcon = StringUtils.getDrawableIdByName(iconName);
    }

    public String getDisplayName() {
        String nickName = new String(alias == null ? "".getBytes() : alias.value);
        String userName = new String(name == null ? "".getBytes() : name.value);

        if (!StringUtils.isEmpty(nickName)) {
            return nickName;
        } else if (!StringUtils.isEmpty(userName)) {
            return userName;
        } else {
            return PkUtils.simplePk(key.key);
        }
    }

    public ToxUserStatus getFriendStatusAsToxUserStatus() {
        return UserStatus.getUserStatusFromString(getStatus());
    }

    public String getFirstLetter() {
        String name = getDisplayName();

        String fLetter = "#";
        String namePinYin =
            CharacterParserUtil.getSelling(name.substring(0, 1)).toUpperCase().substring(0, 1);
        if (!StringUtils.isEmpty(namePinYin) && namePinYin.matches("[A-Z]")) {
            fLetter = namePinYin;
        }
        return fLetter;
    }

    @Override
    public String toString() {
        return "ContactsInfo{"
            + "id="
            + id
            + ", key="
            + key
            + ", name="
            + name
            + ", avatar='"
            + avatar
            + '\''
            + ", online="
            + online
            + ", status='"
            + status
            + '\''
            + ", signature='"
            + signature
            + '\''
            + ", receivedAvatar="
            + receivedAvatar
            + ", blocked="
            + blocked
            + ", mute="
            + mute
            + ", alias="
            + alias
            + ", contactType="
            + contactType
            + ", isBot="
            + isBot
            + ", botType="
            + botType
            + '}';
    }

    public String contactDiffStr() {
        return "ContactsInfo{"
            + "key="
            + key
            + ", name="
            + name
            + ", avatar='"
            + avatar
            + '\''
            + ", online="
            + online
            + ", status='"
            + status
            + '\''
            + ", alias="
            + alias
            + '}';
    }
}
