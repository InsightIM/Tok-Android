package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.enums.ToxConnection;

/**
 * This event is triggered when a friend goes offline after having been online,
 * when a friend goes online, or when the connection type (TCP/UDP) changes.
 *
 * This callback is not called when adding friends. It is assumed that when
 * adding friends, their connection status is initially offline.
 */
public interface FriendConnectionStatusCallback {
    /**
     * @param friendNumber The friend number of the friend whose connection status changed.
     * @param connectionStatus The new connection status.
     */
    void friendConnectionStatus(ToxFriendNumber friendNumber, ToxConnection connectionStatus);
}
