package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

/**
 * wxf add todo
 */
public final class GroupCreateException extends ToxException {

    public GroupCreateException(Code code) {
        this(code, "");
    }

    public GroupCreateException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * No friend with the given Public Key exists on the friend list.
         */
        TOO_LONG,
        /**
         * An argument was null.
         */
        NULL,
    }
}
