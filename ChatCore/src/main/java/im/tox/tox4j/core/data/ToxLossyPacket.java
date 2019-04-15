package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

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
        return "ToxLossyPacket(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
