package im.tox.tox4j.av.data;

public class Height {
    public int value;

    private Height(int value) {
        this.value = value;
    }

    Height unsafeFromInt(int value) {
        return new Height(value);
    }

    public int toInt() {
        return value;
    }
}
