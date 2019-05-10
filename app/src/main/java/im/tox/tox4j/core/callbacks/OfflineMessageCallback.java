package im.tox.tox4j.core.callbacks;

/**
 * This event is triggered when a offline message from offlinebot
 */
public interface OfflineMessageCallback {
    /**
     * @param cmd The detail offline type(cmd)
     * @param data offline message data(to cmd)
     */
    void offlineMessage(int cmd, byte[] data);
}
