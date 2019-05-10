package com.client.tok.msg.callbacks;

import android.content.Context;
import com.client.tok.bean.ContactInfo;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxManager;
import com.client.tok.ui.offlinecore.OfflineHandler;
import com.client.tok.utils.LogUtil;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxLosslessPacket;
import im.tox.tox4j.core.data.ToxLossyPacket;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;

public class ToxCallbackListener implements ToxCoreEventListener {
    private String TAG = "ToxCallbackListener";
    private Context mContext;

    public ToxCallbackListener(Context context) {
        mContext = context;
    }

    AntoxOnSelfConnectionStatusCallback selfConnectionStatusCallback =
        new AntoxOnSelfConnectionStatusCallback();
    AntoxOnConnectionStatusCallback connectionStatusCallback =
        new AntoxOnConnectionStatusCallback();
    AntoxOnTypingChangeCallback typingChangeCallback = new AntoxOnTypingChangeCallback();

    AntoxOnMessageCallback messageCallback = new AntoxOnMessageCallback();
    AntoxOnReadReceiptCallback readReceiptCallback = new AntoxOnReadReceiptCallback();

    AntoxOnFriendRequestCallback friendRequestCallback = new AntoxOnFriendRequestCallback();

    AntoxOnNameChangeCallback nameChangeCallback = new AntoxOnNameChangeCallback();
    AntoxOnSignatureMessageCallback signatureMessageCallback =
        new AntoxOnSignatureMessageCallback();
    AntoxOnUserStatusCallback userStatusCallback = new AntoxOnUserStatusCallback();

    AntoxOnFileRecvCallback fileRecvCallback = new AntoxOnFileRecvCallback();
    AntoxOnFileRecvChunkCallback fileRecvChunkCallback = new AntoxOnFileRecvChunkCallback();
    AntoxOnFileChunkRequestCallback fileChunkRequestCallback =
        new AntoxOnFileChunkRequestCallback();
    AntoxOnFileRecvControlCallback fileRecvControlCallback = new AntoxOnFileRecvControlCallback();
    AntoxOnFriendLosslessPacketCallback friendLosslessPacketCallback =
        new AntoxOnFriendLosslessPacketCallback();

    // --------- tox_file_seek callback is missing here !! ----------
    // tox_file_seek(Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, TOX_ERR_FILE_SEEK *error)
    // --------- tox_file_seek callback is missing here !! ----------
    private ContactInfo getFriendInfo(ToxFriendNumber friendNumber) {
        return State.infoRepo()
            .getFriendInfo(ToxManager.getManager().toxBase.getFriendKey(friendNumber).getKey());
    }

    @Override
    public void friendTyping(ToxFriendNumber friendNumber, boolean isTyping) {
        typingChangeCallback.friendTyping(getFriendInfo(friendNumber), isTyping);
    }

    @Override
    public void fileRecvChunk(ToxFriendNumber friendNumber, int fileNumber, long position,
        byte[] data) {
        LogUtil.i(TAG, "fileRecvChunk...");
        fileRecvChunkCallback.fileRecvChunk(getFriendInfo(friendNumber), fileNumber, position,
            data);
    }

    @Override
    public void fileRecvControl(ToxFriendNumber friendNumber, int fileNumber,
        ToxFileControl control) {
        LogUtil.i(TAG, "fileRecvControl...");
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        fileRecvControlCallback.fileRecvControl(friendInfo, fileNumber, control);
    }

    @Override
    public void friendConnectionStatus(ToxFriendNumber friendNumber,
        ToxConnection connectionStatus) {
        LogUtil.i(TAG, "friendConnectionStatus...");
        connectionStatusCallback.friendConnectionStatus(friendNumber, connectionStatus);
    }

    @Override
    public void friendLosslessPacket(ToxFriendNumber friendNumber, ToxLosslessPacket data) {
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        friendLosslessPacketCallback.friendLosslessPacket(friendInfo, data);
    }

    @Override
    public void friendReadReceipt(ToxFriendNumber friendNumber, int messageId) {
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        readReceiptCallback.friendReadReceipt(friendInfo, messageId);
    }

    @Override
    public void fileChunkRequest(ToxFriendNumber friendNumber, int fileNumber, long position,
        int length) {
        LogUtil.i(TAG, "fileChunkRequest...position:" + position + ",length:" + length);
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        fileChunkRequestCallback.fileChunkRequest(friendInfo, fileNumber, position, length);
    }

    @Override
    public void friendStatusMessage(ToxFriendNumber friendNumber, ToxStatusMessage message) {
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        LogUtil.i(TAG, "friendStatusMessage...");
        signatureMessageCallback.friendStatusMessage(friendInfo, message);
    }

    @Override
    public void friendStatus(ToxFriendNumber friendNumber, ToxUserStatus status) {
        LogUtil.i(TAG, "friendStatus...");
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        userStatusCallback.friendStatus(friendInfo, status);
    }

    @Override
    public void friendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, ToxFriendMessage message) {
        //this method is called in thread
        LogUtil.i(TAG, "friendMessage..."
            + messageType.name()
            + ",message:"
            + new String(message.value)
            + ",time:"
            + timeDelta);
        ContactInfo friendInfo = getFriendInfo(friendNumber);//friend or group
        if (messageType == ToxMessageType.NORMAL) {
            messageCallback.friendMessage(friendInfo, messageType, timeDelta, message);
        }
    }

    @Override
    public void fileRecv(ToxFriendNumber friendNumber, int fileNumber, int kind, long fileSize,
        ToxFilename filename) {
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        LogUtil.i(TAG, "fileReceive...friendNumber:"
            + friendNumber.value
            + ",fileNumber:"
            + fileNumber
            + ",kine:"
            + kind
            + ",fileSize:"
            + fileSize
            + ",fileName:"
            + filename.toString());
        fileRecvCallback.fileRecv(friendInfo, fileNumber, kind, fileSize, filename);
    }

    @Override
    public void selfConnectionStatus(ToxConnection connectionStatus) {
        LogUtil.i(TAG,
            "selfConnection status:" + connectionStatus.name() + ",callback:" + this.hashCode());
        selfConnectionStatusCallback.selfConnectionStatus(connectionStatus);
    }

    @Override
    public void friendName(ToxFriendNumber friendNumber, ToxNickname name) {
        ContactInfo friendInfo = getFriendInfo(friendNumber);
        nameChangeCallback.friendName(friendInfo, name);
    }

    @Override
    public void friendRequest(ToxPublicKey publicKey, int timeDelta,
        ToxFriendRequestMessage message) {
        LogUtil.i(TAG, "friendReqLive");
        friendRequestCallback.friendRequest(publicKey, timeDelta, message);
    }

    /**
     * @param friendNumber The friend number of the friend who sent a lossy packet.
     * @param data A byte array containing the received packet data. The first byte is the packet id.
     */
    @Override
    public void friendLossyPacket(ToxFriendNumber friendNumber, ToxLossyPacket data) {
        //TODO
    }

    @Override
    public void offlineMessage(int cmd, byte[] data) {
        LogUtil.i(TAG, "offlineMessage,cmd:" + cmd);
        OfflineHandler.handle(cmd, data);
    }
}
