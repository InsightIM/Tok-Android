package im.tox.tox4j.core.data;

import com.client.tok.utils.ByteUtil;

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
        return "ToxFileId(" + ByteUtil.bytes2HexStr(value) + ")";
    }

    public String toHexString() {
        return ByteUtil.bytes2HexStr(value);
    }

    public static ToxFileId empty() {
        return new ToxFileId(new byte[] {});
    }
}
