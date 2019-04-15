package im.tox.tox4j.crypto.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxDecryptionException extends ToxException {

    public enum Code {
        /**
         * The input data is missing the magic number (i.e. wasn't created by this
         * module, or is corrupted)
         */
        BAD_FORMAT,
        /**
         * The encrypted byte array could not be decrypted. Either the data was
         * corrupt or the password/key was incorrect.
         */
        FAILED,
        /**
         * The input data was shorter than {@link ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH} bytes.
         */
        INVALID_LENGTH,
        /**
         * The crypto lib was unable to derive a key from the given passphrase,
         * which is usually a lack of memory issue. The functions accepting keys
         * do not produce this error.
         */
        KEY_DERIVATION_FAILED,
        /**
         * The key or input data was null or empty.
         */
        NULL,
    }

    public ToxDecryptionException(Code code) {
        this(code, "");
    }

    public ToxDecryptionException(Code code, String message) {
        super(code, message);
    }
}
