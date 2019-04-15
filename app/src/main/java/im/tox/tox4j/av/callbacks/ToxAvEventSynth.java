package im.tox.tox4j.av.callbacks;

import im.tox.tox4j.av.data.AudioChannels;
import im.tox.tox4j.av.data.BitRate;
import im.tox.tox4j.av.data.Height;
import im.tox.tox4j.av.data.SamplingRate;
import im.tox.tox4j.av.data.Width;
import im.tox.tox4j.av.enums.ToxavFriendCallState;
import im.tox.tox4j.core.data.ToxFriendNumber;

public interface ToxAvEventSynth {
    void invokeAudioBitRate(ToxFriendNumber friendNumber, BitRate audioBitRate);

    void invokeAudioReceiveFrame(ToxFriendNumber friendNumber, short[] pcm, AudioChannels channels,
        SamplingRate samplingRate);

    void invokeCall(ToxFriendNumber friendNumber, boolean audioEnabled, boolean videoEnabled);

    void invokeCallState(ToxFriendNumber friendNumber, ToxavFriendCallState callState);

    void invokeVideoBitRate(ToxFriendNumber friendNumber, BitRate videoBitRate);

    void invokeVideoReceiveFrame(ToxFriendNumber friendNumber, Width width, Height height, byte[] y,
        byte[] u, byte[] v, int yStride, int uStride,
        int vStride);// scalastyle:ignore line.size.limit
}
