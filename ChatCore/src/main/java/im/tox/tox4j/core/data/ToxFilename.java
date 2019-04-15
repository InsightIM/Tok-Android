package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxFilename {
    public byte[] value;

    private ToxFilename(byte[] value) {
        this.value = value;
    }

    public static ToxFilename unsafeFromValue(byte[] value) {
        return new ToxFilename(value);
    }

    @Override
    public String toString() {
        return "ToxFilename(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
