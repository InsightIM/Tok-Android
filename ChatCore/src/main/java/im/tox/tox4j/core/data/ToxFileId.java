package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxFileId {
    public byte[] value;

    private ToxFileId(byte[] value) {
        this.value = value;
    }

    public static ToxFileId unsafeFromValue(byte[] value) {
        return new ToxFileId(value);
    }

    @Override
    public String toString() {
        return "ToxFileId(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }

    public static ToxFileId empty() {
        return new ToxFileId(new byte[] {});
    }
}
