package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendByPublicKeyException extends ToxException {

    public ToxFriendByPublicKeyException(Code code) {
        this(code, "");
    }

    public ToxFriendByPublicKeyException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * No friend with the given Public Key exists on the friend list.
         */
        NOT_FOUND,
        /**
         * An argument was null.
         */
        NULL,
    }
}
