package im.tox.tox4j.av.data;

public class SamplingRate {
    public int value;

    private SamplingRate(int value) {
        this.value = value;
    }

    public SamplingRate unsafeFromInt(int value) {
        return new SamplingRate(value);
    }

    public int toInt() {
        return value;
    }

    public static SamplingRate Rate8k = new SamplingRate(8000);
    public static SamplingRate Rate12k = new SamplingRate(12000);
    public static SamplingRate Rate16k = new SamplingRate(16000);
    public static SamplingRate Rate24k = new SamplingRate(24000);
    public static SamplingRate Rate48k = new SamplingRate(48000);
}
