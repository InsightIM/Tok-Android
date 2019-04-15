package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxSetInfoException extends ToxException {
    public ToxSetInfoException(Code code) {
        this(code, "");
    }

    public ToxSetInfoException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * An argument was null.
         */
        NULL,
        /**
         * Information length exceeded maximum permissible size.
         */
        TOO_LONG,
    }
}
