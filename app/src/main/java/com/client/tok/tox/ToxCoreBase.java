package com.client.tok.tox;

import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.ToxAddress;
import com.client.tok.bean.ToxKey;
import com.client.tok.constant.Intervals;
import com.client.tok.rx.RxBus;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import im.tox.core.network.Port;
import im.tox.proto.Core;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxFriendAddress;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxPublicKey;
import im.tox.tox4j.core.data.ToxSecretKey;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException;
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxGetPortException;
import im.tox.tox4j.core.exceptions.ToxSetInfoException;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.tox4j.impl.jni.ToxCoreImpl;
import im.tox.tox4j.impl.jni.ToxCryptoImpl;
import java.io.File;
import java.util.List;

/**
 * ToxCore, connect with the .so library
 */
public class ToxCoreBase {
    public static int SEND_MSG_FAIL = -1;
    private String TAG = "ToxCoreBase";
    private ToxCoreImpl mToxCoreImpl;
    private ToxConnection mSelfToxConnection = ToxConnection.NONE;
    private String selfKey;

    public ToxCoreBase(ToxOptions options) {
        mToxCoreImpl = new ToxCoreImpl(options == null ? new ToxOptions() : options);
        LogUtil.i(TAG, "ToxCoreBase init ToxCoreImpl has option," + mToxCoreImpl.hashCode());
    }

    public int getInstanceNumber() {
        return mToxCoreImpl.getInstanceNumber();
    }

    public void close() {
        mSelfToxConnection = ToxConnection.NONE;
        RxBus.publish(mSelfToxConnection);
        mToxCoreImpl.close();
        mToxCoreImpl = null;
    }

    public byte[] getSaveData() {
        return mToxCoreImpl.getSaveData();
    }

    public void bootStrap(String address, Port port, ToxPublicKey publicKey)
        throws ToxBootstrapException {
        mToxCoreImpl.bootstrap(address, port, publicKey);
        mToxCoreImpl.addTcpRelay(address, port, publicKey);
    }

    public Port getUdpPort() throws ToxGetPortException {
        return mToxCoreImpl.getUdpPort();
    }

    public Port getTcpPort() throws ToxGetPortException {
        return mToxCoreImpl.getTcpPort();
    }

    public ToxPublicKey getDhtId() {
        return mToxCoreImpl.getDhtId();
    }

    public int iterationInterval() {
        return mToxCoreImpl.iterationInterval();
    }

    public void iterate(ToxCoreEventListener eventListener) {
        try {
            //LogUtil.i(TAG, "ToxCoreBase iterate hashCode," + mToxCoreImpl.hashCode());
            mToxCoreImpl.iterate(eventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int interval() {
        return Intervals.AWAKE.getInterval();
    }

    public ContactsKey getSelfKey() {
        //TODO im.tox.tox4j.exceptions.ToxKilledException: function called on killed tox instance
        try {
            String key = mToxCoreImpl.getSelfPublicKey().toHexString();
            if (key != null && (selfKey == null || !selfKey.equals(key))) {
                selfKey = key;
            }
            LogUtil.i(TAG, "ToxCoreImpl getSelfPublicKey:" + selfKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ContactsKey(selfKey);
    }

    public ToxSecretKey getSecretKey() {
        return mToxCoreImpl.getSecretKey();
    }

    public void setNospam(int nospam) {
        mToxCoreImpl.setNospam(nospam);
    }

    public int getNospam() {
        return mToxCoreImpl.getNospam();
    }

    public ToxAddress getSelfAddress() {
        return new ToxAddress(mToxCoreImpl.getSelfAddress());
    }

    public void setName(ToxNickname nickname) {
        try {
            mToxCoreImpl.setName(nickname);
        } catch (ToxSetInfoException e) {
            e.printStackTrace();
        }
    }

    public ToxNickname getName() {
        return mToxCoreImpl.getName();
    }

    public void setSignature(ToxStatusMessage msg) {
        try {
            mToxCoreImpl.setSignature(msg);
        } catch (ToxSetInfoException e) {
            e.printStackTrace();
        }
    }

    public ToxStatusMessage getSignature() {
        return mToxCoreImpl.getSignature();
    }

    public void setStatus(ToxUserStatus status) {
        mToxCoreImpl.setStatus(status);
    }

    public ToxUserStatus getStatus() {
        return mToxCoreImpl.getStatus();
    }

    public ToxFriendNumber sendAddFriendRequest(ToxAddress address, ToxFriendRequestMessage message)
        throws ToxFriendAddException {
        return mToxCoreImpl.sendAddFriendRequest(
            ToxFriendAddress.unsafeFromValue(address.getBytes()), message);
    }

    public ToxFriendNumber acceptAddFriendRequest(ToxKey key) throws ToxFriendAddException {
        return mToxCoreImpl.acceptAddFriendRequest(ToxPublicKey.unsafeFromValue(key.getBytes()));
    }

    public void deleteFriend(ContactsKey friendKey)
        throws ToxFriendDeleteException, ToxFriendByPublicKeyException {
        mToxCoreImpl.deleteFriend(getFriendNumber(friendKey));
    }

    public ToxFriendNumber getFriendNumber(ContactsKey key) throws ToxFriendByPublicKeyException {
        return mToxCoreImpl.friendByPublicKey(ToxPublicKey.unsafeFromValue(key.getBytes()));
    }

    public ContactsKey getFriendKey(ToxFriendNumber friendNumber) {
        try {
            return new ContactsKey(mToxCoreImpl.getFriendPublicKey(friendNumber).toHexString());
        } catch (ToxFriendGetPublicKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean friendExists(ContactsKey friendKey) throws ToxFriendByPublicKeyException {
        return mToxCoreImpl.friendExists(getFriendNumber(friendKey));
    }

    public List<ContactsKey> getFriendList() {
        return mToxCoreImpl.getFriendKeys();
    }

    public void setTyping(ContactsKey friendKey, boolean typing) {
        try {
            mToxCoreImpl.setTyping(getFriendNumber(friendKey), typing);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int friendSendMessage(ContactsKey receiverKey, ToxFriendMessage message,
        ToxMessageType messageType) {
        try {
            return mToxCoreImpl.friendSendMessage(getFriendNumber(receiverKey), messageType, 0,
                message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SEND_MSG_FAIL;
    }

    public int friendSendMessageOffline(ContactsKey receiverKey,
        Core.FriendMessageOffline offlineInfo) {
        try {
            return mToxCoreImpl.friendSendMessageOffline(getFriendNumber(receiverKey),
                offlineInfo.getCmd(), 0, offlineInfo.getMessage().toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SEND_MSG_FAIL;
    }

    public byte[] hash(byte[] bytes) {
        return ToxCryptoImpl.hash(bytes);
    }

    public byte[] hashFile(File file) {
        return hash(FileUtilsJ.readToBytes(file));
    }

    public void fileControl(ContactsKey friendKey, int fileNumber, ToxFileControl control) {
        try {
            mToxCoreImpl.fileControl(getFriendNumber(friendKey), fileNumber, control);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int fileSend(ContactsKey friendKey, int kind, long fileSize, ToxFileId fileId,
        ToxFilename filename) {
        try {
            return mToxCoreImpl.fileSend(getFriendNumber(friendKey), kind, fileSize, fileId,
                filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SEND_MSG_FAIL;
    }

    public void fileSendChunk(ContactsKey friendKey, int fileNumber, long position, byte[] data) {
        try {
            mToxCoreImpl.fileSendChunk(getFriendNumber(friendKey), fileNumber, position, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ToxFileId fileGetFileId(ContactsKey friendKey, int fileNumber) {
        try {
            return mToxCoreImpl.getFileFileId(getFriendNumber(friendKey), fileNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encryptMsg(int friendNumber, byte[] bytes) {
        byte[] result = mToxCoreImpl.encryptMsg(friendNumber, bytes);
        LogUtil.i(TAG, "before encrypt:" + bytes.length + ",after encrypt:" + result.length);
        return result;
    }

    public byte[] decryptMsg(int friendNumber, byte[] bytes) {
        byte[] result = mToxCoreImpl.decryptMsg(friendNumber, bytes);
        LogUtil.i(TAG, "before decrypt:" + bytes.length + ",after decrypt:" + result.length);
        return result;
    }

    public long generateUniqueId(ContactsKey friendKey) {
        try {
            return mToxCoreImpl.generateUniqueId(getFriendNumber(friendKey).value);
        } catch (ToxFriendByPublicKeyException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
