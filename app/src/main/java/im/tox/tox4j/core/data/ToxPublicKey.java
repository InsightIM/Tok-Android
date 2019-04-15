package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxPublicKey(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
