package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxFriendMessage {
    public byte[] value;

    private ToxFriendMessage(byte[] value) {
        this.value = value;
    }

    public static ToxFriendMessage unsafeFromValue(byte[] value) {
        return new ToxFriendMessage(value);
    }

    @Override
    public String toString() {
        return "ToxFriendMessage(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }

    public boolean validate() {
        return value != null && value.length > 0;
    }
}
