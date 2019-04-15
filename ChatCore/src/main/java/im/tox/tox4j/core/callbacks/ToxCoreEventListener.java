package im.tox.tox4j.core.callbacks;

public interface ToxCoreEventListener
    extends SelfConnectionStatusCallback, FileRecvControlCallback, FileRecvCallback,
    FileRecvChunkCallback, FileChunkRequestCallback, FriendConnectionStatusCallback,
    FriendMessageCallback, FriendNameCallback, FriendRequestCallback, FriendStatusCallback,
    FriendStatusMessageCallback, FriendTypingCallback, FriendLosslessPacketCallback,
    FriendLossyPacketCallback, FriendReadReceiptCallback {
}
