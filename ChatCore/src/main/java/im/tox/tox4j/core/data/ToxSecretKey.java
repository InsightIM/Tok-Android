package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxSecretKey {
    private byte[] value;

    private ToxSecretKey(byte[] value) {
        this.value = value;
    }

    public static ToxSecretKey unsafeFromValue(byte[] value) {
        return new ToxSecretKey(value);
    }

    @Override
    public String toString() {
        return "ToxSecretKey(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
