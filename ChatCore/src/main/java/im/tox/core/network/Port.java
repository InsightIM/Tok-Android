package im.tox.core.network;

public class Port {
    public int value;

    private Port(int value) {
        this.value = value;
    }

    public static Port unsafeFromInt(int value) {
        return new Port(value);
    }

    public int toInt() {
        return value;
    }
}
