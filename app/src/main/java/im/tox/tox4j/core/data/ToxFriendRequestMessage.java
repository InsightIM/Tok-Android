package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxFriendRequestMessage(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
