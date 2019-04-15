package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.JavaOnly;
import im.tox.tox4j.exceptions.ToxException;

public final class ToxBootstrapException extends ToxException {
    public ToxBootstrapException(Code code) {
        this(code, "");
    }

    public ToxBootstrapException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The public key was of invalid length.
         */
        @JavaOnly BAD_KEY,
        /**
         * The address could not be resolved to an IP address, or the IP address
         * passed was invalid.
         */
        BAD_HOST,
        /**
         * The port passed was invalid. The valid port range is (1, 65535).
         */
        BAD_PORT,
        /**
         * An argument was null.
         */
        NULL,
    }
}

