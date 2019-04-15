package im.tox.tox4j.core.data;

import scala.Serializable;

public class ToxNickname implements Serializable {
    public byte[] value;

    private ToxNickname(byte[] value) {
        this.value = value;
    }

    public static ToxNickname unsafeFromValue(byte[] value) {
        return new ToxNickname(value);
    }

    @Override
    public String toString() {
        return new String(this.value);
    }
}
