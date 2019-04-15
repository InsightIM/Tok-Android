package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

/**
 * such as Signature
 */
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
        return value == null ? "" : new String(value);
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
