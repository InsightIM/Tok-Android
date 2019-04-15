package im.tox.tox4j.av.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * Triggered when a friend calls us.
 */
public interface CallCallback {
    /**
     * @param friendNumber The friend number from which the call is incoming.
     * @param audioEnabled True if friend is sending audio.
     * @param videoEnabled True if friend is sending video.
     */
    boolean call(ToxFriendNumber friendNumber, Boolean audioEnabled, Boolean videoEnabled);
}
