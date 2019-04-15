package im.tox.tox4j.av.data;

public class Width {
    public int value;

    private Width(int value) {
        this.value = value;
    }

    public Width unsafeFromInt(int value) {
        return new Width(value);
    }

    public int toInt() {
        return value;
    }
}
