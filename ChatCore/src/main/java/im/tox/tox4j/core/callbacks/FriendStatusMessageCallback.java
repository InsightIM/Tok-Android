package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxStatusMessage;

/**
 * This event is triggered when a friend changes their status message.
 */
public interface FriendStatusMessageCallback {
    /**
     * @param friendNumber The friend number of the friend whose status message changed.
     * @param message The new status message.
     */
    void friendStatusMessage(ToxFriendNumber friendNumber, ToxStatusMessage message);
}
