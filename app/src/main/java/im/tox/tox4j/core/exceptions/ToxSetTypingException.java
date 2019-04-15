package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxSetTypingException extends ToxException {

    public ToxSetTypingException(Code code) {
        this(code, "");
    }

    public ToxSetTypingException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
    }
}
