package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;

/**
 * This event is triggered when the friend receives the message sent with
 * [[ToxCore.friendSendMessage]] with the corresponding message ID.
 */
public interface FriendReadReceiptCallback {
    /**
     * @param friendNumber The friend number of the friend who received the message.
     * @param messageId The message ID as returned from [[ToxCore.friendSendMessage]] corresponding to the message sent.
     */
    void friendReadReceipt(ToxFriendNumber friendNumber, int messageId);
}
