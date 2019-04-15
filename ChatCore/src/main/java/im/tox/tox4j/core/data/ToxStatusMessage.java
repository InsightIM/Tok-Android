package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxStatusMessage {
    public byte[] value;

    private ToxStatusMessage(byte[] value) {
        this.value = value;
    }

    public static ToxStatusMessage unsafeFromValue(byte[] value) {
        return new ToxStatusMessage(value);
    }

    @Override
    public String toString() {
        return "ToxStatusMessage(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
