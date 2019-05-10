package im.tox.tox4j.impl.jni;

import com.client.tok.BuildConfig;
import com.client.tok.bean.ContactsKey;
import com.client.tok.utils.ByteUtil;
import com.client.tok.utils.LogUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import im.tox.core.network.Port;
import im.tox.tox4j.core.IToxCore;
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
import im.tox.tox4j.impl.jni.proto.ToxLog;
import im.tox.utils.LogCoreUtil;
import java.util.ArrayList;
import java.util.List;

public class ToxCoreImpl implements IToxCore {
    private String TAG = "ToxCoreImpl";
    private int instanceNumber;
    private ToxOptions options;

    public ToxCoreImpl(ToxOptions options) {
        this.options = options;
        initNumber();
    }

    private void initNumber() {
        try {
            instanceNumber = ToxCoreJni.toxNew(options.ipv6Enabled, options.udpEnabled,
                options.localDiscoveryEnabled, options.proxy.proxyType.ordinal(),
                options.proxy.proxyAddress, options.proxy.proxyPort, options.startPort,
                options.endPort, options.tcpPort, options.saveData.kind.ordinal(),
                options.saveData.data);
            baseLog("initNumber");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public IToxCore load(ToxOptions options) throws ToxNewException {
        LogCoreUtil.i(TAG, "ToxCoreImpl load");
        return new ToxCoreImpl(options);
    }

    @Override
    public void close() {
        LogCoreUtil.i(TAG, "ToxCoreImpl close toxKill," + instanceNumber);
        try {
            baseLog("finalize");
            ToxCoreJni.toxKill(instanceNumber);
            ToxCoreJni.toxFinalize(instanceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException {
        baseLog("bootstrap");
        ToxCoreJni.toxBootstrap(instanceNumber, address, port.value, publicKey.value);
    }

    @Override
    public void addTcpRelay(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException {
        baseLog("addTcpRelay");
        ToxCoreJni.toxAddTcpRelay(instanceNumber, address, port.value, publicKey.value);
    }

    @Override
    public byte[] getSaveData() {
        baseLog("getSaveData");
        return ToxCoreJni.toxGetSavedata(instanceNumber);
    }

    @Override
    public Port getUdpPort() throws ToxGetPortException {
        baseLog("getUdpPort");
        return Port.unsafeFromInt(ToxCoreJni.toxSelfGetUdpPort(instanceNumber));
    }

    @Override
    public Port getTcpPort() throws ToxGetPortException {
        baseLog("getTcpPort");
        return Port.unsafeFromInt(ToxCoreJni.toxSelfGetTcpPort(instanceNumber));
    }

    @Override
    public ToxPublicKey getDhtId() {
        baseLog("getDhtId");
        return ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetDhtId(instanceNumber));
    }

    @Override
    public int iterationInterval() {
        baseLog("iterationInterval");
        return ToxCoreJni.toxIterationInterval(instanceNumber);
    }

    //iterate ToxCoreEventListener 将原来的iterate方法改为iteration方法，在java中是关键字
    @Override
    public void iterate(ToxCoreEventListener handler) {
        LogUtil.i(TAG, "ToxCoreImpl instanceNumber:" + instanceNumber);
        baseLog("iterate");
        ToxCoreEventDispatch.dispatch(handler, ToxCoreJni.toxIterate(instanceNumber));
    }

    @Override
    public ToxPublicKey getSelfPublicKey() {
        baseLog("getSelfPublicKey");
        return ToxPublicKey.unsafeFromValue(ToxCoreJni.toxSelfGetPublicKey(instanceNumber));
    }

    @Override
    public ToxSecretKey getSecretKey() {
        baseLog("getSecretKey");
        return ToxSecretKey.unsafeFromValue(ToxCoreJni.toxSelfGetSecretKey(instanceNumber));
    }

    @Override
    public void setNospam(int nospam) {
        baseLog("setNospam");
        ToxCoreJni.toxSelfSetNospam(instanceNumber, nospam);
    }

    @Override
    public int getNospam() {
        baseLog("getNospam");
        LogCoreUtil.i(TAG, "ToxCoreImpl getNospam,instanceNumber" + instanceNumber);
        return ToxCoreJni.toxSelfGetNospam(instanceNumber);
    }

    @Override
    public byte[] getSelfAddress() {
        baseLog("getSelfAddress");
        return ToxCoreJni.toxSelfGetAddress(instanceNumber);
    }

    @Override
    public void setName(ToxNickname name) throws ToxSetInfoException {
        baseLog("setName");
        ToxCoreJni.toxSelfSetName(instanceNumber, name.value);
    }

    @Override
    public ToxNickname getName() {
        baseLog("getName");
        return ToxNickname.unsafeFromValue(ToxCoreJni.toxSelfGetName(instanceNumber));
    }

    @Override
    public void setSignature(ToxStatusMessage message) throws ToxSetInfoException {
        baseLog("setSignature");
        ToxCoreJni.toxSelfSetStatusMessage(instanceNumber, message.value);
    }

    @Override
    public ToxStatusMessage getSignature() {
        baseLog("getSignature");
        return ToxStatusMessage.unsafeFromValue(ToxCoreJni.toxSelfGetStatusMessage(instanceNumber));
    }

    @Override
    public void setStatus(ToxUserStatus status) {
        baseLog("setStatus");
        ToxCoreJni.toxSelfSetStatus(instanceNumber, status.ordinal());
    }

    @Override
    public ToxUserStatus getStatus() {
        baseLog("getStatus");
        return ToxUserStatus.values()[ToxCoreJni.toxSelfGetStatus(instanceNumber)];
    }

    @Override
    public ToxFriendNumber sendAddFriendRequest(ToxFriendAddress address,
        ToxFriendRequestMessage message) throws ToxFriendAddException, IllegalArgumentException {
        baseLog("sendAddFriendRequest");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendAdd(instanceNumber, address.value, message.value));
    }

    @Override
    public ToxFriendNumber acceptAddFriendRequest(ToxPublicKey publicKey)
        throws ToxFriendAddException, IllegalArgumentException {
        baseLog("acceptAddFriendRequest");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendAddNorequest(instanceNumber, publicKey.value));
    }

    @Override
    public void deleteFriend(ToxFriendNumber friendNumber) throws ToxFriendDeleteException {
        LogCoreUtil.i(TAG, "ToxCoreImpl deleteFriend,instanceNumber" + instanceNumber);
        ToxCoreJni.toxFriendDelete(instanceNumber, friendNumber.value);
    }

    @Override
    public ToxFriendNumber friendByPublicKey(ToxPublicKey publicKey)
        throws ToxFriendByPublicKeyException {
        baseLog("friendByPublicKey");
        return ToxFriendNumber.unsafeFromInt(
            ToxCoreJni.toxFriendByPublicKey(instanceNumber, publicKey.value));
    }

    @Override
    public ToxPublicKey getFriendPublicKey(ToxFriendNumber friendNumber)
        throws ToxFriendGetPublicKeyException {
        baseLog("getFriendPublicKey");
        return ToxPublicKey.unsafeFromValue(
            ToxCoreJni.toxFriendGetPublicKey(instanceNumber, friendNumber.value));
    }

    @Override
    public Boolean friendExists(ToxFriendNumber friendNumber) {
        LogCoreUtil.i(TAG, "ToxCoreImpl friendExists");
        return ToxCoreJni.toxFriendExists(instanceNumber, friendNumber.value);
    }

    @Override
    public int[] getFriendList() {
        LogCoreUtil.i(TAG, "ToxCoreImpl getFriendList");
        return ToxCoreJni.toxSelfGetFriendList(instanceNumber);
    }

    @Override
    public List<ContactsKey> getFriendKeys() {
        LogCoreUtil.i(TAG, "ToxCoreImpl getFriendNumbers");
        int[] friendIds = getFriendList();
        List<ContactsKey> keys = new ArrayList<>();
        if (friendIds != null) {
            for (int i = 0; i < friendIds.length; i++) {
                ToxFriendNumber friendNumber = ToxFriendNumber.unsafeFromInt(friendIds[i]);
                try {
                    keys.add(new ContactsKey(getFriendPublicKey(friendNumber).toHexString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return keys;
    }

    @Override
    public void setTyping(ToxFriendNumber friendNumber, Boolean typing)
        throws ToxSetTypingException {
        LogCoreUtil.i(TAG, "ToxCoreImpl setTyping");
        ToxCoreJni.toxSelfSetTyping(instanceNumber, friendNumber.value, typing);
    }

    @Override
    public int friendSendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, ToxFriendMessage message) throws ToxFriendSendMessageException {
        LogCoreUtil.i(TAG, "ToxCoreImpl friendSendMessage");
        return ToxCoreJni.toxFriendSendMessage(instanceNumber, friendNumber.value,
            messageType.getType(), timeDelta, message.value);
    }

    @Override
    public int friendSendMessageOffline(ToxFriendNumber friendNumber, int cmd, int timeDelta,
        byte[] message) throws ToxFriendSendMessageException {
        LogCoreUtil.i(TAG, "ToxCoreImpl friendSendOffMessage,cmd:" + cmd);
        return ToxCoreJni.toxFriendSendMessageOffline(instanceNumber, friendNumber.value, cmd,
            timeDelta, message);
    }

    @Override
    public void fileControl(ToxFriendNumber friendNumber, int fileNumber, ToxFileControl control)
        throws ToxFileControlException {
        LogCoreUtil.i(TAG, "ToxCoreImpl fileControl " + control.name() + ",hashcode:" + hashCode());
        ToxCoreJni.toxFileControl(instanceNumber, friendNumber.value, fileNumber,
            control.ordinal());
    }

    @Override
    public void fileSeek(ToxFriendNumber friendNumber, int fileNumber, Long position)
        throws ToxFileSeekException {
        LogCoreUtil.i(TAG, "ToxCoreImpl fileSeek");
        ToxCoreJni.toxFileSeek(instanceNumber, friendNumber.value, fileNumber, position);
    }

    @Override
    public ToxFileId getFileFileId(ToxFriendNumber friendNumber, int fileNumber)
        throws ToxFileGetException {
        LogCoreUtil.i(TAG, "ToxCoreImpl getFileFileId");
        return ToxFileId.unsafeFromValue(
            ToxCoreJni.toxFileGetFileId(instanceNumber, friendNumber.value, fileNumber));
    }

    @Override
    public int fileSend(ToxFriendNumber friendNumber, int kind, Long fileSize, ToxFileId fileId,
        ToxFilename filename) throws ToxFileSendException {
        LogCoreUtil.i(TAG, "ToxCoreImpl fileSend");
        return ToxCoreJni.toxFileSend(instanceNumber, friendNumber.value, kind, fileSize,
            (fileId == null ? ByteUtil.int2OneByte(0) : fileId.value), filename.value);
    }

    @Override
    public void fileSendChunk(ToxFriendNumber friendNumber, int fileNumber, Long position,
        byte[] data) throws ToxFileSendChunkException {
        LogCoreUtil.i(TAG, "ToxCoreImpl fileSendChunk");
        ToxCoreJni.toxFileSendChunk(instanceNumber, friendNumber.value, fileNumber, position, data);
    }

    @Override
    public void friendSendLossyPacket(ToxFriendNumber friendNumber, ToxLossyPacket data)
        throws ToxFriendCustomPacketException {
        LogCoreUtil.i(TAG, "ToxCoreImpl friendSendLossyPacket");
        ToxCoreJni.toxFriendSendLossyPacket(instanceNumber, friendNumber.value, data.value);
    }

    @Override
    public void friendSendLosslessPacket(ToxFriendNumber friendNumber, ToxLosslessPacket data)
        throws ToxFriendCustomPacketException {
        LogCoreUtil.i(TAG, "ToxCoreImpl friendSendLosslessPacket");
        ToxCoreJni.toxFriendSendLosslessPacket(instanceNumber, friendNumber.value, data.value);
    }

    @Override
    public void invokeFriendName(ToxFriendNumber friendNumber, ToxNickname name) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendName");
        ToxCoreJni.invokeFriendName(instanceNumber, friendNumber.value, name.value);
    }

    @Override
    public void invokeFriendStatusMessage(ToxFriendNumber friendNumber, byte[] message) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendStatusMessage");
        ToxCoreJni.invokeFriendStatusMessage(instanceNumber, friendNumber.value, message);
    }

    @Override
    public void invokeFriendStatus(ToxFriendNumber friendNumber, ToxUserStatus status) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendStatus");
        ToxCoreJni.invokeFriendStatus(instanceNumber, friendNumber.value, status.ordinal());
    }

    @Override
    public void invokeFriendConnectionStatus(ToxFriendNumber friendNumber,
        ToxConnection connectionStatus) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendConnectionStatus");
        ToxCoreJni.invokeFriendConnectionStatus(instanceNumber, friendNumber.value,
            connectionStatus.ordinal());
    }

    @Override
    public void invokeFriendTyping(ToxFriendNumber friendNumber, Boolean isTyping) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendTyping");
        ToxCoreJni.invokeFriendTyping(instanceNumber, friendNumber.value, isTyping);
    }

    @Override
    public void invokeFriendReadReceipt(ToxFriendNumber friendNumber, int messageId) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendReadReceipt");
        ToxCoreJni.invokeFriendReadReceipt(instanceNumber, friendNumber.value, messageId);
    }

    @Override
    public void invokeFriendRequest(ToxPublicKey publicKey, int timeDelta, byte[] message) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendRequest");
        ToxCoreJni.invokeFriendRequest(instanceNumber, publicKey.value, timeDelta, message);
    }

    @Override
    public void invokeFriendMessage(ToxFriendNumber friendNumber, ToxMessageType messageType,
        int timeDelta, byte[] message) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendMessage");
        ToxCoreJni.invokeFriendMessage(instanceNumber, friendNumber.value, messageType.getType(),
            timeDelta, message);
    }

    @Override
    public void invokeFileChunkRequest(ToxFriendNumber friendNumber, int fileNumber, Long position,
        int length) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFileChunkRequest");
        ToxCoreJni.invokeFileChunkRequest(instanceNumber, friendNumber.value, fileNumber, position,
            length);
    }

    @Override
    public void invokeFileRecv(ToxFriendNumber friendNumber, int fileNumber, int kind,
        Long fileSize, byte[] filename) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFileRecv");
        ToxCoreJni.invokeFileRecv(instanceNumber, friendNumber.value, fileNumber, kind, fileSize,
            filename);
    }

    @Override
    public void invokeFileRecvChunk(ToxFriendNumber friendNumber, int fileNumber, Long position,
        byte[] data) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFileRecvChunk");
        ToxCoreJni.invokeFileRecvChunk(instanceNumber, friendNumber.value, fileNumber, position,
            data);
    }

    @Override
    public void invokeFileRecvControl(ToxFriendNumber friendNumber, int fileNumber,
        ToxFileControl control) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFileRecvControl");
        ToxCoreJni.invokeFileRecvControl(instanceNumber, friendNumber.value, fileNumber,
            control.ordinal());
    }

    @Override
    public void invokeFriendLossyPacket(ToxFriendNumber friendNumber, byte[] data) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendLossyPacket");
        ToxCoreJni.invokeFriendLossyPacket(instanceNumber, friendNumber.value, data);
    }

    @Override
    public void invokeFriendLosslessPacket(ToxFriendNumber friendNumber, byte[] data) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeFriendLosslessPacket");
        ToxCoreJni.invokeFriendLosslessPacket(instanceNumber, friendNumber.value, data);
    }

    @Override
    public void invokeSelfConnectionStatus(ToxConnection connectionStatus) {
        LogCoreUtil.i(TAG, "ToxCoreImpl invokeSelfConnectionStatus");
        ToxCoreJni.invokeSelfConnectionStatus(instanceNumber, connectionStatus.ordinal());
    }

    @Override
    public void tox4jLastLog() {
        if (BuildConfig.LOG_DEBUG) {
            try {
                ToxLog.JniLog jniLog = ToxLog.JniLog.parseFrom(ToxCoreJni.tox4jLastLog());
                for (ToxLog.JniLogEntry entry : jniLog.getEntriesList()) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("entryName:" + entry.getName());
                    for (ToxLog.Value value : entry.getArgumentsList()) {
                        sb.append("\n");
                        sb.append("value Truncated:" + value.getTruncated());
                        sb.append(",VCase:" + value.getVCase().name());
                    }

                    ToxLog.Value result = entry.getResult();
                    sb.append("\n");
                    sb.append("result Truncated:" + result.getTruncated());
                    sb.append(",VCase:" + result.getVCase().name());

                    LogUtil.i("toxLog", "Entry------" + sb.toString());
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] encryptMsg(int friendNumber, byte[] bytes) {
        baseLog("encryptMsg,friendNumber:" + friendNumber);
        return ToxCoreJni.toxEncryptMessageOffline(instanceNumber, friendNumber, bytes);
    }

    @Override
    public byte[] decryptMsg(int friendNumber, byte[] bytes) {
        baseLog("decryptMsg,friendNumber:" + friendNumber);
        return ToxCoreJni.toxDecryptMessageOffline(instanceNumber, friendNumber, bytes);
    }

    @Override
    public long generateUniqueId(int friendNumber) {
        baseLog("generateUniqueId,friendNumber:" + friendNumber);
        return ToxCoreJni.toxLocalMsgId(instanceNumber, friendNumber);
    }

    private void baseLog(String method) {
        LogUtil.i(TAG, method
            + ",instanceNumber:"
            + instanceNumber
            + ",toxcoreImple hashcode:"
            + this.hashCode());
    }
}
