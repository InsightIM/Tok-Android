package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxFriendMessage(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }

    public boolean validate() {
        return value != null && value.length > 0;
    }
}
