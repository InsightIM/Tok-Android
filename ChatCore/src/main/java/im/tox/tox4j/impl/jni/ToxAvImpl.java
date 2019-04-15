package im.tox.tox4j.impl.jni;

import im.tox.tox4j.av.ToxAv;
import im.tox.tox4j.av.callbacks.ToxAvEventListener;
import im.tox.tox4j.av.data.AudioChannels;
import im.tox.tox4j.av.data.BitRate;
import im.tox.tox4j.av.data.Height;
import im.tox.tox4j.av.data.SampleCount;
import im.tox.tox4j.av.data.SamplingRate;
import im.tox.tox4j.av.data.Width;
import im.tox.tox4j.av.enums.ToxavCallControl;
import im.tox.tox4j.av.enums.ToxavFriendCallState;
import im.tox.tox4j.av.exceptions.ToxavAnswerException;
import im.tox.tox4j.av.exceptions.ToxavBitRateSetException;
import im.tox.tox4j.av.exceptions.ToxavCallControlException;
import im.tox.tox4j.av.exceptions.ToxavCallException;
import im.tox.tox4j.av.exceptions.ToxavNewException;
import im.tox.tox4j.av.exceptions.ToxavSendFrameException;
import im.tox.tox4j.core.ToxCore;
import im.tox.tox4j.core.data.ToxFriendNumber;

public class ToxAvImpl implements ToxAv {
    private ToxCoreImpl toxCore;
    private int instanceNumber;

    public ToxAvImpl(ToxCoreImpl toxCore) {
        try {
            this.toxCore = toxCore;
            instanceNumber = ToxAvJni.toxavNew(toxCore.instanceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ToxAv create(ToxCore tox) throws ToxavNewException {
        try {
            return new ToxAvImpl((ToxCoreImpl) tox);
        } catch (ClassCastException e) {
            throw new ToxavNewException(ToxavNewException.Code.INCOMPATIBLE, tox.toString());
        }
    }

    @Override
    public void close() {
        //tox.removeOnCloseCallback(onClose)
        ToxAvJni.toxavKill(instanceNumber);
    }

    protected void finalize() {
        try {
            ToxAvJni.toxavFinalize(instanceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int iterationInterval() {
        return ToxAvJni.toxavIterationInterval(instanceNumber);
    }

    @Override
    public void iterate(ToxAvEventListener handler) {
        ToxAvEventDispatch.dispatch(handler, ToxAvJni.toxavIterate(instanceNumber));
    }

    @Override
    public void call(ToxFriendNumber friendNumber, BitRate audioBitRate, BitRate videoBitRate)
        throws ToxavCallException {
        ToxAvJni.toxavCall(instanceNumber, friendNumber.value, audioBitRate.value,
            videoBitRate.value);
    }

    @Override
    public void answer(ToxFriendNumber friendNumber, BitRate audioBitRate, BitRate videoBitRate)
        throws ToxavAnswerException {
        ToxAvJni.toxavAnswer(instanceNumber, friendNumber.value, audioBitRate.value,
            videoBitRate.value);
    }

    @Override
    public void callControl(ToxFriendNumber friendNumber, ToxavCallControl control)
        throws ToxavCallControlException {
        ToxAvJni.toxavCallControl(instanceNumber, friendNumber.value, control.ordinal());
    }

    @Override
    public void setAudioBitRate(ToxFriendNumber friendNumber, BitRate audioBitRate)
        throws ToxavBitRateSetException {
        ToxAvJni.toxavAudioSetBitRate(instanceNumber, friendNumber.value, audioBitRate.value);
    }

    @Override
    public void setVideoBitRate(ToxFriendNumber friendNumber, BitRate videoBitRate)
        throws ToxavBitRateSetException {
        ToxAvJni.toxavVideoSetBitRate(instanceNumber, friendNumber.value, videoBitRate.value);
    }

    @Override
    public void audioSendFrame(ToxFriendNumber friendNumber, short[] pcm, SampleCount sampleCount,
        AudioChannels channels, SamplingRate samplingRate) throws ToxavSendFrameException {
        ToxAvJni.toxavAudioSendFrame(instanceNumber, friendNumber.value, pcm, sampleCount.value,
            channels.value, samplingRate.value);
    }

    @Override
    public void videoSendFrame(ToxFriendNumber friendNumber, int width, int height, byte[] y,
        byte[] u, byte[] v) throws ToxavSendFrameException {
        ToxAvJni.toxavVideoSendFrame(instanceNumber, friendNumber.value, width, height, y, u, v);
    }

    @Override
    public void invokeAudioBitRate(ToxFriendNumber friendNumber, BitRate audioBitRate) {
        ToxAvJni.invokeAudioBitRate(instanceNumber, friendNumber.value, audioBitRate.value);
    }

    @Override
    public void invokeAudioReceiveFrame(ToxFriendNumber friendNumber, short[] pcm,
        AudioChannels channels, SamplingRate samplingRate) {
        ToxAvJni.invokeAudioReceiveFrame(instanceNumber, friendNumber.value, pcm, channels.value,
            samplingRate.value);
    }

    @Override
    public void invokeCall(ToxFriendNumber friendNumber, boolean audioEnabled,
        boolean videoEnabled) {
        ToxAvJni.invokeCall(instanceNumber, friendNumber.value, audioEnabled, videoEnabled);
    }

    @Override
    public void invokeCallState(ToxFriendNumber friendNumber, ToxavFriendCallState callState) {
        ToxAvJni.invokeCallState(instanceNumber, friendNumber.value,
            ToxAvEventDispatch.convert(callState));
    }

    @Override
    public void invokeVideoBitRate(ToxFriendNumber friendNumber, BitRate videoBitRate) {
        ToxAvJni.invokeVideoBitRate(instanceNumber, friendNumber.value, videoBitRate.value);
    }

    @Override
    public void invokeVideoReceiveFrame(ToxFriendNumber friendNumber, Width width, Height height,
        byte[] y, byte[] u, byte[] v, int yStride, int uStride, int vStride) {
        ToxAvJni.invokeVideoReceiveFrame(instanceNumber, friendNumber.value, width.value,
            height.value, y, u, v, yStride, uStride, vStride);
    }
}
