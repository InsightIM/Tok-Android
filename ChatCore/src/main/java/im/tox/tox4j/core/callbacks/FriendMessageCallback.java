package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.enums.ToxMessageType;

/**
 * This event is triggered when a message from a friend is received.
 */
public interface FriendMessageCallback {
    /**
     * @param friendNumber The friend number of the friend who sent the message.
     * @param messageType Message type (normal, action, ...).
     * @param timeDelta A delta in seconds between when the message was composed
     * and when it is being transmitted. For messages that are sent immediately,
     * it will be 0. If a message was written and couldn't be sent immediately
     * (due to a connection failure, for example), the timeDelta is an
     * approximation of when it was composed.
     * @param message The message data they sent.
     */
    void friendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType, int timeDelta,
        ToxFriendMessage message);
}
