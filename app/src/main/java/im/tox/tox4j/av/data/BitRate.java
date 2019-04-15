package im.tox.tox4j.av.data;

public class BitRate {
    public int value;

    private BitRate(int value) {
        this.value = value;
    }

    public static BitRate Unchanged = new BitRate(-1);
    public static BitRate Disabled = new BitRate(0);

    public static BitRate unsafeFromInt(int value) {
        return new BitRate(value);
    }

    public int toInt() {
        return value;
    }
}
