package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxLossyPacket;

/**
 * This event is triggered when a custom lossy packet arrives from a friend.
 */
public interface FriendLossyPacketCallback {
    /**
     * @param friendNumber The friend number of the friend who sent a lossy packet.
     * @param data A byte array containing the received packet data. The first byte is the packet id.
     */
    void friendLossyPacket(ToxFriendNumber friendNumber, ToxLossyPacket data);
}
