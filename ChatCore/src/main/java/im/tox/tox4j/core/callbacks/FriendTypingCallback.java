package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * This event is triggered when a friend starts or stops typing.
 */
public interface FriendTypingCallback {
    /**
     * @param friendNumber The friend number of the friend who started or stopped typing.
     * @param isTyping Whether the friend started (true) or stopped (false) typing.
     */
    void friendTyping(ToxFriendNumber friendNumber, Boolean isTyping);
}
