package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendAddException extends ToxException {
    public ToxFriendAddException(Code code) {
        this(code, "");
    }

    public ToxFriendAddException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * A friend request has already been sent, or the address belongs to a friend
         * that is already on the friend list. To resend a friend request, first remove
         * the friend, and then call addFriend again.
         */
        ALREADY_SENT,
        /**
         * The friend address checksum failed.
         */
        BAD_CHECKSUM,
        /**
         * A memory allocation failed when trying to increase the friend list size.
         */
        MALLOC,
        /**
         * The friend request message was empty. This, and the TOO_LONG code will
         * never be returned from {@link ToxCore#addFriendNoRequest}.
         */
        NO_MESSAGE,
        /**
         * An argument was null.
         */
        NULL,
        /**
         * The friend address belongs to the sending client.
         */
        OWN_KEY,
        /**
         * The friend was already on the friend list, but the noSpam value was different.
         */
        SET_NEW_NOSPAM,
        /**
         * The length of the friend request message exceeded {@link ToxCoreConstants#MAX_FRIEND_REQUEST_LENGTH}.
         */
        TOO_LONG,
    }
}
