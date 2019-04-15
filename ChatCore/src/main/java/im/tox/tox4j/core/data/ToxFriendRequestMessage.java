package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxFriendRequestMessage {
    public byte[] value;

    private ToxFriendRequestMessage(byte[] value) {
        this.value = value;
    }

    public static ToxFriendRequestMessage unsafeFromValue(byte[] value) {
        return new ToxFriendRequestMessage(value);
    }

    @Override
    public String toString() {
        return "ToxFriendRequestMessage(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
