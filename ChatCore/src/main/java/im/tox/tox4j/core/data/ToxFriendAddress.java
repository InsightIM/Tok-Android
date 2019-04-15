package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxFriendAddress {
    public byte[] value;

    private ToxFriendAddress(byte[] value) {
        this.value = value;
    }

    public static ToxFriendAddress unsafeFromValue(byte[] value) {
        return new ToxFriendAddress(value);
    }

    @Override
    public String toString() {
        return "ToxFriendAddress(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
