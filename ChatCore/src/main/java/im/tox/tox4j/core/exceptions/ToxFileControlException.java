package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFileControlException extends ToxException {
    public ToxFileControlException(Code code) {
        this(code, "");
    }

    public ToxFileControlException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * A {@link ToxFileControl#PAUSE} control was sent, but the file transfer was already paused.
         */
        ALREADY_PAUSED,
        /**
         * A {@link ToxFileControl#RESUME} control was sent, but the file transfer was paused by the other
         * party. Only the party that paused the transfer can resume it.
         */
        DENIED,
        /**
         * This client is currently not connected to the friend.
         */
        FRIEND_NOT_CONNECTED,
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * No file transfer with the given file number was found for the given friend.
         */
        NOT_FOUND,
        /**
         * A {@link ToxFileControl#RESUME} control was sent, but the file transfer is running normally.
         */
        NOT_PAUSED,
        /**
         * An allocation error occurred while increasing the send queue size.
         */
        SENDQ,
    }
}
