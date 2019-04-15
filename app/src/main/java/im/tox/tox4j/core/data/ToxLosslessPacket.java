package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

public class ToxLosslessPacket {
    public byte[] value;

    private ToxLosslessPacket(byte[] value) {
        this.value = value;
    }

    public static ToxLosslessPacket unsafeFromValue(byte[] value) {
        return new ToxLosslessPacket(value);
    }

    @Override
    public String toString() {
        return "ToxLosslessPacket(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
