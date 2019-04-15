package im.tox.tox4j.av.data;

public class AudioChannels {
    public int value;

    private AudioChannels(int value) {
        this.value = value;
    }

    public static AudioChannels unsafeFromInt(int value) {
        return new AudioChannels(value);
    }

    public int toInt() {
        return value;
    }
}
