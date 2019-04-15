package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxSecretKey(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
