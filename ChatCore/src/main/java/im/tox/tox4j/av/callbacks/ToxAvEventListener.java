package im.tox.tox4j.av.callbacks;

public interface ToxAvEventListener
    extends CallCallback, CallStateCallback, AudioBitRateCallback, VideoBitRateCallback,
    AudioReceiveFrameCallback, VideoReceiveFrameCallback {
}
