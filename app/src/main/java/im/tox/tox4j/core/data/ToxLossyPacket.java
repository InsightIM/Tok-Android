package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

public class ToxLossyPacket {
    public byte[] value;

    private ToxLossyPacket(byte[] value) {
        this.value = value;
    }

    public static ToxLossyPacket unsafeFromValue(byte[] value) {
        return new ToxLossyPacket(value);
    }

    @Override
    public String toString() {
        return "ToxLossyPacket(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
