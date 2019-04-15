package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

/**
 * wxf group新增
 */
public final class GroupSendMessageException extends ToxException {

    public GroupSendMessageException(Code code) {
        this(code, "");
    }

    public GroupSendMessageException(Code code, String message) {
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
        GROUP_NOT_CONNECTED,
        /**
         * The friend number did not designate a valid friend.
         */
        GROUP_NOT_FOUND,
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
