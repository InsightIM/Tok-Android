package im.tox.tox4j.av.data;

public class SampleCount {
    public int value;

    private SampleCount(int value) {
        this.value = value;
    }

    public SampleCount(AudioLength audioLength, SamplingRate samplingRate) {
        value = (samplingRate.value / 1000 * audioLength.value);
    }
}
