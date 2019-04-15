package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxLosslessPacket;

/**
 * This event is triggered when a custom lossless packet arrives from a friend.
 */
public interface FriendLosslessPacketCallback {
    /**
     * @param friendNumber The friend number of the friend who sent a lossless packet.
     * @param data A byte array containing the received packet data. The first byte is the packet id.
     */
    void friendLosslessPacket(ToxFriendNumber friendNumber, ToxLosslessPacket data);
}
