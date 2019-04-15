package im.tox.tox4j.av.data;

public class AudioLength {
    public int value;

    private AudioLength(int value) {
        this.value = value;
    }

    public static AudioLength Length2_5 = new AudioLength(2500);
    public static AudioLength Length5 = new AudioLength(5000);
    public static AudioLength Length10 = new AudioLength(10000);
    public static AudioLength Length20 = new AudioLength(20000);
    public static AudioLength Length40 = new AudioLength(40000);
    public static AudioLength Length60 = new AudioLength(60000);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AudioLength) {
            return ((AudioLength) obj).value == value;
        }
        return super.equals(obj);
    }

    public boolean equals(AudioLength a, AudioLength b) {
        return a.value == b.value;
    }
}
