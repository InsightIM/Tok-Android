package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

public class GroupMessage {
    public byte[] value;

    private GroupMessage(byte[] value) {
        this.value = value;
    }

    public static GroupMessage unsafeFromValue(byte[] value) {
        return new GroupMessage(value);
    }

    @Override
    public String toString() {
        return "GroupMessage(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }

    public boolean validate() {
        return value != null && value.length > 0;
    }
}
