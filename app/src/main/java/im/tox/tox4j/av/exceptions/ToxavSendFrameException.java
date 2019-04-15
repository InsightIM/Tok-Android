package im.tox.tox4j.av.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxavSendFrameException extends ToxException {

    public enum Code {
        /**
         * The friend number did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * This client is currently not in a call with the friend.
         */
        FRIEND_NOT_IN_CALL,
        /**
         * One or more of the frame parameters was invalid. E.g. the resolution may be too
         * small or too large, or the audio sampling rate may be unsupported.
         */
        INVALID,
        /**
         * In case of video, one of Y, U, or V was NULL. In case of audio, the samples
         * data pointer was NULL.
         */
        NULL,
        /**
         * Either friend turned off audio or video receiving or we turned off sending
         * for the said payload.
         */
        PAYLOAD_TYPE_DISABLED,
        /**
         * Failed to push frame through rtp interface.
         */
        RTP_FAILED,
        /**
         * Synchronization error occurred.
         */
        SYNC,
    }

    public ToxavSendFrameException(Code code) {
        this(code, "");
    }

    public ToxavSendFrameException(Code code, String message) {
        super(code, message);
    }
}
