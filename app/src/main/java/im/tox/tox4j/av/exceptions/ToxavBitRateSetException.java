package im.tox.tox4j.av.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxavBitRateSetException extends ToxException {

    public enum Code {
        /**
         * The friend_number passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * This client is currently not in a call with the friend.
         */
        FRIEND_NOT_IN_CALL,
        /**
         * The bit rate passed was not one of the supported values.
         */
        INVALID_BIT_RATE,
        /**
         * Synchronization error occurred.
         */
        SYNC,
    }

    public ToxavBitRateSetException(Code code) {
        this(code, "");
    }

    public ToxavBitRateSetException(Code code, String message) {
        super(code, message);
    }
}
