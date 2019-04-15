package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.exceptions.ToxException;

public final class ToxFriendCustomPacketException extends ToxException {
    public ToxFriendCustomPacketException(Code code) {
        this(code, "");
    }

    public ToxFriendCustomPacketException(Code code, String message) {
        super(code, message);
    }

    public enum Code {
        /**
         * Attempted to send an empty packet.
         */
        EMPTY,
        /**
         * This client is currently not connected to the friend.
         */
        FRIEND_NOT_CONNECTED,
        /**
         * The friendNumber passed did not designate a valid friend.
         */
        FRIEND_NOT_FOUND,
        /**
         * The first byte of data was not in the specified range for the packet type.
         * This range is 200-254 for lossy, and 160-191 for lossless packets.
         */
        INVALID,
        /**
         * An argument was null.
         */
        NULL,
        /**
         * An allocation error occurred while increasing the send queue size.
         */
        SENDQ,
        /**
         * Packet data length exceeded {@link ToxCoreConstants#MAX_CUSTOM_PACKET_SIZE}.
         */
        TOO_LONG,
    }
}
