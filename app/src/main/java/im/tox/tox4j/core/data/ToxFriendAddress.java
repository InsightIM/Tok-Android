package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxFriendAddress(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
