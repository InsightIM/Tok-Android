package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

/**
 * wxf add todo
 */
public final class GroupByPublicKeyException extends ToxException {

    public GroupByPublicKeyException(Code code) {
        this(code, "");
    }

    public GroupByPublicKeyException(Code code, String message) {
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
