package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendGetPublicKeyException extends ToxException {
    public ToxFriendGetPublicKeyException(Code code) {
        this(code, "");
    }

    public ToxFriendGetPublicKeyException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
    }
}
