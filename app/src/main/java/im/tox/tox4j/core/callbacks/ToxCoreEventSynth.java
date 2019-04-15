package im.tox.tox4j.core.callbacks;

import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;

public interface ToxCoreEventSynth {

    void invokeFriendName(ToxFriendNumber friendNumber, ToxNickname name);

    void invokeFriendStatusMessage(ToxFriendNumber friendNumber, byte[] message);

    void invokeFriendStatus(ToxFriendNumber friendNumber, ToxUserStatus status);

    void invokeFriendConnectionStatus(ToxFriendNumber friendNumber, ToxConnection connectionStatus);

    void invokeFriendTyping(ToxFriendNumber friendNumber, Boolean isTyping);

    void invokeFriendReadReceipt(ToxFriendNumber friendNumber, int messageId);

    void invokeFriendRequest(ToxPublicKey publicKey, int timeDelta, byte[] message);

    void invokeFriendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, byte[] message);

    void invokeFileChunkRequest(ToxFriendNumber friendNumber, int fileNumber, Long position,
        int length);

    void invokeFileRecv(ToxFriendNumber friendNumber, int fileNumber, int kind, Long fileSize,
        byte[] filename);

    void invokeFileRecvChunk(ToxFriendNumber friendNumber, int fileNumber, Long position,
        byte[] data);

    void invokeFileRecvControl(ToxFriendNumber friendNumber, int fileNumber,
        ToxFileControl control);

    void invokeFriendLossyPacket(ToxFriendNumber friendNumber, byte[] data);

    void invokeFriendLosslessPacket(ToxFriendNumber friendNumber, byte[] data);

    void invokeSelfConnectionStatus(ToxConnection connectionStatus);
}
