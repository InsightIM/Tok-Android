package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

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
        return "ToxLosslessPacket(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
