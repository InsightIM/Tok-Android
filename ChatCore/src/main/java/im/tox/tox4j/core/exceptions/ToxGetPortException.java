package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxGetPortException extends ToxException {
    public ToxGetPortException(Code code) {
        this(code, "");
    }

    public ToxGetPortException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The instance was not bound to any port.
         */
        NOT_BOUND,
    }
}
