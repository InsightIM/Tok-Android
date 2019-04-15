package im.tox.tox4j.impl.jni;

import im.tox.core.network.Port;
import im.tox.tox4j.core.ToxCore;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendAddress;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxLosslessPacket;
import im.tox.tox4j.core.data.ToxLossyPacket;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.data.ToxSecretKey;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxFileControlException;
import im.tox.tox4j.core.exceptions.ToxFileGetException;
import im.tox.tox4j.core.exceptions.ToxFileSeekException;
import im.tox.tox4j.core.exceptions.ToxFileSendChunkException;
import im.tox.tox4j.core.exceptions.ToxFileSendException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendCustomPacketException;
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException;
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException;
import im.tox.tox4j.core.exceptions.ToxGetPortException;
import im.tox.tox4j.core.exceptions.ToxNewException;
import im.tox.tox4j.core.exceptions.ToxSetInfoException;
import im.tox.tox4j.core.exceptions.ToxSetTypingException;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.utils.LogUtils;

public class ToxCoreImpl implements ToxCore {
    private String TAG = "ToxCoreImpl";
    public int instanceNumber;
    private ToxOptions options;

    public ToxCoreImpl(ToxOptions options) {
        this.options = options;
        initNumber();
        LogUtils.i(TAG, "ToxCoreImpl construction method");
    }

    private void initNumber() {
        try {
            instanceNumber = ToxCoreJni.toxNew(options.ipv6Enabled, options.udpEnabled,
                options.localDiscoveryEnabled, options.proxy.proxyType.ordinal(),
                options.proxy.proxyAddress, options.proxy.proxyPort, options.startPort,
                options.endPort, options.tcpPort, options.saveData.kind.ordinal(),
                options.saveData.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ToxCore load(ToxOptions options) throws ToxNewException {
        LogUtils.i(TAG, "ToxCoreImpl load");
        return new ToxCoreImpl(options);
    }

    @Override
    public void close() {
        LogUtils.i(TAG, "ToxCoreImpl close");
        ToxCoreJni.toxKill(instanceNumber);
    }

    @Override
    public void finalize() {
        try {
            LogUtils.i(TAG, "ToxCoreImpl finalize");
            close();
            ToxCoreJni.toxFinalize(instanceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException {
        LogUtils.i(TAG, "ToxCoreImpl bootstrap");
        ToxCoreJni.toxBootstrap(instanceNumber, address, port.value, publicKey.value);
    }

    @Override
    public void addTcpRelay(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException {
        LogUtils.i(TAG, "ToxCoreImpl addTcpRelay");
        ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port.value, publicKey.value);
    }

    @Override
    public byte[] getSavedata() {
        LogUtils.i(TAG, "ToxCoreImpl getSavedata");
        return ToxCoreJni.toxGetSavedata(instanceNumber);
    }

    @Override
    public Port getUdpPort() throws ToxGetPortException {
        LogUtils.i(TAG, "ToxCoreImpl getUdpPort");
        return Port.unsafeFromInt(ToxCoreJni.toxSelfGetUdpPort(instanceNumber));
    }

    @Override
    public Port getTcpPort() throws ToxGetPortException {
        LogUtils.i(TAG, "ToxCoreImpl getTcpPort");
        return Port.unsafeFromInt(ToxCoreJni.toxSelfGetTcpPort(instanceNumber));
    }

    @Override
    public ToxPublicKey getDhtId() {
        LogUtils.i(TAG, "ToxCoreImpl getDhtId");
        return ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetDhtId(instanceNumber));
    }

    @Override
    public int iterationInterval() {
        LogUtils.i(TAG, "ToxCoreImpl iterationInterval");
        return ToxCoreJni.toxIterationInterval(instanceNumber);
    }

    //iterate ToxCoreEventListener 将原来的iterate方法改为iteration方法，在java中是关键字
    @Override
    public void iterate(ToxCoreEventListener handler) {
        LogUtils.i(TAG, "ToxCoreImpl iteration");
        ToxCoreEventDispatch.dispatch(handler, ToxCoreJni.toxIterate(instanceNumber));
    }

    @Override
    public ToxPublicKey getPublicKey() {
        LogUtils.i(TAG, "ToxCoreImpl getPublicKey");
        return ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetPublicKey(instanceNumber));
    }

    @Override
    public ToxSecretKey getSecretKey() {
        LogUtils.i(TAG, "ToxCoreImpl getSecretKey");
        return ToxSecretKey.unsafeFromValue(ToxCoreJni.toxSelfGetSecretKey(instanceNumber));
    }

    @Override
    public void setNospam(int nospam) {
        LogUtils.i(TAG, "ToxCoreImpl setNospam");
        ToxCoreJni.toxSelfSetNospam(instanceNumber, nospam);
    }

    @Override
    public int getNospam() {
        LogUtils.i(TAG, "ToxCoreImpl getNospam");
        return ToxCoreJni.toxSelfGetNospam(instanceNumber);
    }

    @Override
    public ToxFriendAddress getAddress() {
        LogUtils.i(TAG, "ToxCoreImpl getAddress");
        return ToxFriendAddress.unsafeFromValue(ToxCoreJni.toxSelfGetAddress(instanceNumber));
    }

    @Override
    public void setName(ToxNickname name) throws ToxSetInfoException {
        LogUtils.i(TAG, "ToxCoreImpl setName");
        ToxCoreJni.toxSelfSetName(instanceNumber, name.value);
    }

    @Override
    public ToxNickname getName() {
        LogUtils.i(TAG, "ToxCoreImpl getName");
        return ToxNickname.unsafeFromValue(ToxCoreJni.toxSelfGetName(instanceNumber));
    }

    @Override
    public void setStatusMessage(ToxStatusMessage message) throws ToxSetInfoException {
        LogUtils.i(TAG, "ToxCoreImpl setStatusMessage");
        ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, message.value);
    }

    @Override
    public ToxStatusMessage getStatusMessage() {
        LogUtils.i(TAG, "ToxCoreImpl getStatusMessage");
        return ToxStatusMessage.unsafeFromValue(ToxCoreJni.toxSelfGetStatusMessage(instanceNumber));
    }

    @Override
    public void setStatus(ToxUserStatus status) {
        LogUtils.i(TAG, "ToxCoreImpl setStatus");
        ToxCoreJni.toxSelfSetStatus(instanceNumber, status.ordinal());
    }

    @Override
    public ToxUserStatus getStatus() {
        LogUtils.i(TAG, "ToxCoreImpl getStatus");
        return ToxUserStatus.values()[ToxCoreJni.toxSelfGetStatus(instanceNumber)];
    }

    @Override
    public ToxFriendNumber addFriend(ToxFriendAddress address, ToxFriendRequestMessage message)
        throws ToxFriendAddException, IllegalArgumentException {
        LogUtils.i(TAG, "ToxCoreImpl addFriend");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value));
    }

    @Override
    public ToxFriendNumber addFriendNorequest(ToxPublicKey publicKey)
        throws ToxFriendAddException, IllegalArgumentException {
        LogUtils.i(TAG, "ToxCoreImpl addFriendNorequest");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value));
    }

    @Override
    public void deleteFriend(ToxFriendNumber friendNumber) throws ToxFriendDeleteException {
        LogUtils.i(TAG, "ToxCoreImpl deleteFriend");
        ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber.value);
    }

    @Override
    public ToxFriendNumber friendByPublicKey(ToxPublicKey publicKey)
        throws ToxFriendByPublicKeyException {
        LogUtils.i(TAG, "ToxCoreImpl friendByPublicKey");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value));
    }

    @Override
    public ToxPublicKey getFriendPublicKey(ToxFriendNumber friendNumber)
        throws ToxFriendGetPublicKeyException {
        LogUtils.i(TAG, "ToxCoreImpl getFriendPublicKey");
        return ToxPublicKey.unsafeFromValue(
            ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber.value));
    }

    @Override
    public Boolean friendExists(ToxFriendNumber friendNumber) {
        LogUtils.i(TAG, "ToxCoreImpl friendExists");
        return ToxCoreJni.toxFriendExists(instanceNumber, friendNumber.value);
    }

    @Override
    public int[] getFriendList() {
        LogUtils.i(TAG, "ToxCoreImpl getFriendList");
        return ToxCoreJni.toxSelfGetFriendList(instanceNumber);
    }

    @Override
    public ToxFriendNumber[] getFriendNumbers() {
        LogUtils.i(TAG, "ToxCoreImpl getFriendNumbers");
        int[] friendIds = getFriendList();
        ToxFriendNumber[] friendNumbers;
        if (friendIds != null) {
            friendNumbers = new ToxFriendNumber[friendIds.length];
            for (int i = 0; i < friendIds.length; i++) {
                friendNumbers[i] = ToxFriendNumber.unsafeFromInt(friendIds[i]);
            }
            return friendNumbers;
        }
        return null;
    }

    @Override
    public void setTyping(ToxFriendNumber friendNumber, Boolean typing)
        throws ToxSetTypingException {
        LogUtils.i(TAG, "ToxCoreImpl setTyping");
        ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber.value, typing);
    }

    @Override
    public int friendSendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, ToxFriendMessage message) throws ToxFriendSendMessageException {
        LogUtils.i(TAG, "ToxCoreImpl friendSendMessage");
        return ToxCoreJni.toxFriendSendMessage(instanceNumber, friendNumber.value,
            messageType.ordinal(), timeDelta, message.value);
    }

    @Override
    public void fileControl(ToxFriendNumber friendNumber, int fileNumber, ToxFileControl control)
        throws ToxFileControlException {
        LogUtils.i(TAG, "ToxCoreImpl fileControl");
        ToxCoreJni.toxFileControl(instanceNumber, friendNumber.value, fileNumber,
            control.ordinal());
    }

    @Override
    public void fileSeek(ToxFriendNumber friendNumber, int fileNumber, Long position)
        throws ToxFileSeekException {
        LogUtils.i(TAG, "ToxCoreImpl fileSeek");
        ToxCoreJni.toxFileSeek(instanceNumber, friendNumber.value, fileNumber, position);
    }

    @Override
    public ToxFileId getFileFileId(ToxFriendNumber friendNumber, int fileNumber)
        throws ToxFileGetException {
        LogUtils.i(TAG, "ToxCoreImpl getFileFileId");
        return ToxFileId.unsafeFromValue(
            ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber.value, fileNumber));
    }

    @Override
    public int fileSend(ToxFriendNumber friendNumber, int kind, Long fileSize, ToxFileId fileId,
        ToxFilename filename) throws ToxFileSendException {
        LogUtils.i(TAG, "ToxCoreImpl fileSend");
        return ToxCoreJni.toxFileSend(instanceNumber, friendNumber.value, kind, fileSize,
            fileId.value, filename.value);
    }

    @Override
    public void fileSendChunk(ToxFriendNumber friendNumber, int fileNumber, Long position,
        byte[] data) throws ToxFileSendChunkException {
        LogUtils.i(TAG, "ToxCoreImpl fileSendChunk");
        ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber.value, fileNumber, position, data);
    }

    @Override
    public void friendSendLossyPacket(ToxFriendNumber friendNumber, ToxLossyPacket data)
        throws ToxFriendCustomPacketException {
        LogUtils.i(TAG, "ToxCoreImpl friendSendLossyPacket");
        ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber.value, data.value);
    }

    @Override
    public void friendSendLosslessPacket(ToxFriendNumber friendNumber, ToxLosslessPacket data)
        throws ToxFriendCustomPacketException {
        LogUtils.i(TAG, "ToxCoreImpl friendSendLosslessPacket");
        ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber.value, data.value);
    }

    @Override
    public void invokeFriendName(ToxFriendNumber friendNumber, ToxNickname name) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendName");
        ToxCoreJni.invokeFriendName(instanceNumber, friendNumber.value, name.value);
    }

    @Override
    public void invokeFriendStatusMessage(ToxFriendNumber friendNumber, byte[] message) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendStatusMessage");
        ToxCoreJni.invokeFriendStatusMessage(instanceNumber, friendNumber.value, message);
    }

    @Override
    public void invokeFriendStatus(ToxFriendNumber friendNumber, ToxUserStatus status) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendStatus");
        ToxCoreJni.invokeFriendStatus(instanceNumber, friendNumber.value, status.ordinal());
    }

    @Override
    public void invokeFriendConnectionStatus(ToxFriendNumber friendNumber,
        ToxConnection connectionStatus) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendConnectionStatus");
        ToxCoreJni.invokeFriendConnectionStatus(instanceNumber, friendNumber.value,
            connectionStatus.ordinal());
    }

    @Override
    public void invokeFriendTyping(ToxFriendNumber friendNumber, Boolean isTyping) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendTyping");
        ToxCoreJni.invokeFriendTyping(instanceNumber, friendNumber.value, isTyping);
    }

    @Override
    public void invokeFriendReadReceipt(ToxFriendNumber friendNumber, int messageId) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendReadReceipt");
        ToxCoreJni.invokeFriendReadReceipt(instanceNumber, friendNumber.value, messageId);
    }

    @Override
    public void invokeFriendRequest(ToxPublicKey publicKey, int timeDelta, byte[] message) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendRequest");
        ToxCoreJni.invokeFriendRequest(instanceNumber, publicKey.value, timeDelta, message);
    }

    @Override
    public void invokeFriendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, byte[] message) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendMessage");
        ToxCoreJni.invokeFriendMessage(instanceNumber, friendNumber.value, messageType.ordinal(),
            timeDelta, message);
    }

    @Override
    public void invokeFileChunkRequest(ToxFriendNumber friendNumber, int fileNumber, Long position,
        int length) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFileChunkRequest");
        ToxCoreJni.invokeFileChunkRequest(instanceNumber, friendNumber.value, fileNumber, position,
            length);
    }

    @Override
    public void invokeFileRecv(ToxFriendNumber friendNumber, int fileNumber, int kind,
        Long fileSize, byte[] filename) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFileRecv");
        ToxCoreJni.invokeFileRecv(instanceNumber, friendNumber.value, fileNumber, kind, fileSize,
            filename);
    }

    @Override
    public void invokeFileRecvChunk(ToxFriendNumber friendNumber, int fileNumber, Long position,
        byte[] data) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFileRecvChunk");
        ToxCoreJni.invokeFileRecvChunk(instanceNumber, friendNumber.value, fileNumber, position,
            data);
    }

    @Override
    public void invokeFileRecvControl(ToxFriendNumber friendNumber, int fileNumber,
        ToxFileControl control) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFileRecvControl");
        ToxCoreJni.invokeFileRecvControl(instanceNumber, friendNumber.value, fileNumber,
            control.ordinal());
    }

    @Override
    public void invokeFriendLossyPacket(ToxFriendNumber friendNumber, byte[] data) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendLossyPacket");
        ToxCoreJni.invokeFriendLossyPacket(instanceNumber, friendNumber.value, data);
    }

    @Override
    public void invokeFriendLosslessPacket(ToxFriendNumber friendNumber, byte[] data) {
        LogUtils.i(TAG, "ToxCoreImpl invokeFriendLosslessPacket");
        ToxCoreJni.invokeFriendLosslessPacket(instanceNumber, friendNumber.value, data);
    }

    @Override
    public void invokeSelfConnectionStatus(ToxConnection connectionStatus) {
        LogUtils.i(TAG, "ToxCoreImpl invokeSelfConnectionStatus");
        ToxCoreJni.invokeSelfConnectionStatus(instanceNumber, connectionStatus.ordinal());
    }
}
