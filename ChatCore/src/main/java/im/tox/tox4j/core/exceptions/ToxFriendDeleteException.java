package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendDeleteException extends ToxException {
    public ToxFriendDeleteException(Code code) {
        this(code, "");
    }

    public ToxFriendDeleteException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * There was no friend with the given friend number. No friends were deleted.
         */
        FRIEND_NOT_FOUND,
    }
}
