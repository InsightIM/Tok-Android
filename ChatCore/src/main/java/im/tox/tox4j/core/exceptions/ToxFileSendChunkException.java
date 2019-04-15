package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFileSendChunkException extends ToxException {
    public ToxFileSendChunkException(Code code) {
        this(code, "");
    }

    public ToxFileSendChunkException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * This client is currently not connected to the friend.
         */
        FRIEND_NOT_CONNECTED,
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * Attempted to send more or less data than requested. The requested data size is
         * adjusted according to maximum transmission unit and the expected end of
         * the file. Trying to send less or more than requested will return this error.
         */
        INVALID_LENGTH,
        /**
         * No file transfer with the given file number was found for the given friend.
         */
        NOT_FOUND,
        /**
         * File transfer was found but isn't in a transferring state: (paused, done,
         * broken, etc...) (happens only when not called from the request chunk callback).
         */
        NOT_TRANSFERRING,
        /**
         * An argument was null.
         */
        NULL,
        /**
         * An allocation error occurred while increasing the send queue size.
         */
        SENDQ,
        /**
         * Position parameter was wrong.
         */
        WRONG_POSITION,
    }
}
