package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return new String(value);
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }
}
