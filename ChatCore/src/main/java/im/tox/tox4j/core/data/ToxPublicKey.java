package im.tox.tox4j.core.data;

import im.tox.tox4j.utils.StrUtil;

public class ToxPublicKey {
    public byte[] value;

    private ToxPublicKey(byte[] value) {
        this.value = value;
    }

    public static ToxPublicKey unsafeFromValue(byte[] value) {
        return new ToxPublicKey(value);
    }

    @Override
    public String toString() {
        return "ToxPublicKey(" + StrUtil.byteArrayToHexStr(value) + ")";
    }

    public String toHexString() {
        return StrUtil.byteArrayToHexStr(value);
    }
}
