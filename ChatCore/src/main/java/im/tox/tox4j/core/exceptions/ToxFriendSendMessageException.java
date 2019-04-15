package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendSendMessageException extends ToxException {

    public ToxFriendSendMessageException(Code code) {
        this(code, "");
    }

    public ToxFriendSendMessageException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * Attempted to send a zero-length message.
         */
        EMPTY,
        /**
         * This client is currently not connected to the friend.
         */
        FRIEND_NOT_CONNECTED,
        /**
         * The friend number did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * An argument was null.
         */
        NULL,
        /**
         * An allocation error occurred while increasing the send queue size.
         */
        SENDQ,
        /**
         * Message length exceeded {@link ToxCoreConstants#MAX_MESSAGE_LENGTH}.
         */
        TOO_LONG,
    }
}
