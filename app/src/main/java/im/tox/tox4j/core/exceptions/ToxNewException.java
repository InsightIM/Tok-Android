package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxNewException extends ToxException {

    public ToxNewException(Code code) {
        this(code, "");
    }

    public ToxNewException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The data format was invalid. This can happen when loading data that was
         * saved by an older version of Tox, or when the data has been corrupted.
         * When loading from badly formatted data, some data may have been loaded,
         * and the rest is discarded. Passing an invalid length parameter also
         * causes this error.
         */
        LOAD_BAD_FORMAT,
        /**
         * The byte array to be loaded contained an encrypted save.
         */
        LOAD_ENCRYPTED,
        /**
         * The function was unable to allocate enough memory to store the internal
         * structures for the Tox object.
         */
        MALLOC,
        /**
         * An argument was null.
         */
        NULL,
        /**
         * The function was unable to bind to a port. This may mean that all ports
         * have already been bound, e.g. by other Tox instances, or it may mean
         * a permission error. You may be able to gather more information from errno.
         */
        PORT_ALLOC,
        /**
         * {@link im.tox.tox4j.core.ToxOptions#proxyType} was valid,
         * but the {@link im.tox.tox4j.core.ToxOptions#proxyAddress} passed had an invalid format.
         */
        PROXY_BAD_HOST,
        /**
         * {@link im.tox.tox4j.core.ToxOptions#proxyType} was valid,
         * but the {@link im.tox.tox4j.core.ToxOptions#proxyPort} was invalid.
         */
        PROXY_BAD_PORT,
        /**
         * {@link im.tox.tox4j.core.ToxOptions#proxyType} was invalid.
         */
        PROXY_BAD_TYPE,
        /**
         * The proxy address passed could not be resolved.
         */
        PROXY_NOT_FOUND,
    }
}

