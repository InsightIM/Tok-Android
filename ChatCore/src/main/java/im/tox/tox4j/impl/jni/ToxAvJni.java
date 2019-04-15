package im.tox.tox4j.impl.jni;

import im.tox.tox4j.av.exceptions.ToxavAnswerException;
import im.tox.tox4j.av.exceptions.ToxavBitRateSetException;
import im.tox.tox4j.av.exceptions.ToxavCallControlException;
import im.tox.tox4j.av.exceptions.ToxavCallException;
import im.tox.tox4j.av.exceptions.ToxavNewException;
import im.tox.tox4j.av.exceptions.ToxavSendFrameException;

public final class ToxAvJni {
    static {
        ToxLoadJniLibrary.load("tox4j-c");
    }

    static native int toxavNew(int toxInstanceNumber) throws ToxavNewException;

    static native void toxavKill(int instanceNumber);

    static native void toxavFinalize(int instanceNumber);

    static native int toxavIterationInterval(int instanceNumber);

    static native byte[] toxavIterate(int instanceNumber);

    static native void toxavCall(int instanceNumber, int friendNumber, int audioBitRate,
        int videoBitRate) throws ToxavCallException;

    static native void toxavAnswer(int instanceNumber, int friendNumber, int audioBitRate,
        int videoBitRate) throws ToxavAnswerException;

    static native void toxavCallControl(int instanceNumber, int friendNumber, int control)
        throws ToxavCallControlException;

    static native void toxavAudioSetBitRate(int instanceNumber, int friendNumber, int audioBitRate)
        throws ToxavBitRateSetException;

    static native void toxavVideoSetBitRate(int instanceNumber, int friendNumber, int videoBitRate)
        throws ToxavBitRateSetException;

    static native void toxavAudioSendFrame(int instanceNumber, int friendNumber, short[] pcm,
        int sampleCount, int channels, int samplingRate) throws ToxavSendFrameException;

    @SuppressWarnings("checkstyle:parametername")
    static native void toxavVideoSendFrame(int instanceNumber, int friendNumber, int width,
        int height, byte[] y, byte[] u, byte[] v) throws ToxavSendFrameException;

    static native void invokeAudioReceiveFrame(int instanceNumber, int friendNumber, short[] pcm,
        int channels, int samplingRate);

    static native void invokeAudioBitRate(int instanceNumber, int friendNumber, int audioBitRate);

    static native void invokeVideoBitRate(int instanceNumber, int friendNumber, int videoBitRate);

    static native void invokeCall(int instanceNumber, int friendNumber, boolean audioEnabled,
        boolean videoEnabled);

    static native void invokeCallState(int instanceNumber, int friendNumber, int callState);

    @SuppressWarnings("checkstyle:parametername")
    static native void invokeVideoReceiveFrame(int instanceNumber, int friendNumber, int width,
        int height, byte[] y, byte[] u, byte[] v, int yStride, int uStride, int vStride);
}
