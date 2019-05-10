package com.client.tok.transfer;

import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.ToxAddress;
import com.client.tok.constant.FileKind;
import com.client.tok.constant.Intervals;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxCoreBase;
import com.client.tok.ui.filecore.TransFileInfo;
import com.client.tok.ui.filecore.TransFilePbUtil;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import com.client.tok.utils.StringUtils;
import im.tox.tox4j.core.data.ToxFileId;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxFileControl;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferManager {
    private String TAG = "TransferManager";
    private ToxManager coreManager = ToxManager.getManager();
    /**
     * key  dbId
     * value fileTransfer
     */
    private Map<Long, FileTransfer> transferMap = new HashMap<>();

    /**
     * find dbId by key and fileId
     * key = ContactsKey+FileId
     * value = dbId
     */
    private Map<String, Long> keyAndFileId2DbId = new HashMap<>();

    /**
     * avatar file incoming
     */
    public void avatarIncomingRequest(ContactsKey key, ToxNickname senderName, boolean hasBeenRead,
        int fileNumber, String fileName, FileKind fileKind, long fileSize,
        boolean replaceExisting) {
        fileIncomingRequest(key, key, key, senderName, hasBeenRead, fileNumber, fileName, fileKind,
            fileSize, 0, replaceExisting);
    }

    /**
     * add a new incoming file to userRepository and to the transferManager
     *
     * @param key real receiver
     * @param proxyContactsKey proxy receiver(message assistant) when the file is off line file
     * @param senderKey file sender(your friend)
     * @param senderName Name of the friend sending the file.
     * @param hasBeenRead the file has been show on the chat page
     * @param fileNumber file number from tox
     * @param path the full path where the file will be save
     * @param fileKind file type(normal fail & avatar)
     * @param fileSize file size(bytes)
     * @param createTime send file timestamp(milli second)
     * @param replaceExisting Whether or not to replace an existing file with the same name.
     */
    public void fileIncomingRequest(ContactsKey key, ContactsKey proxyContactsKey,
        ContactsKey senderKey, ToxNickname senderName, boolean hasBeenRead, int fileNumber,
        String path, FileKind fileKind, long fileSize, long createTime, boolean replaceExisting) {
        //1.delete the existing file
        if (replaceExisting) {
            FileUtilsJ.delFile(path);
        }
        //2.create folders for the incoming file
        FileUtilsJ.createFolders(path, true);

        //3.save file message to userRepository
        long dbId = State.infoRepo()
            .addFileTransfer(fileNumber, key, senderKey, senderName, path, createTime,
                GlobalParams.SEND_ING, GlobalParams.RECEIVE_ING, hasBeenRead, fileSize, fileKind);
        //4.add to transferManager
        State.transferManager()
            .add(new FileTransfer(proxyContactsKey, key, new File(path), fileNumber, fileSize, 0,
                false, FileStatus.REQUEST_SENT, dbId, fileKind, true));
    }

    /**
     * updata my avatar to custom friend
     *
     * @param friendKey custom friend key
     */
    public void updateSelfAvatar2Friend(String friendKey) {
        ContactInfo friend = State.infoRepo().getFriendInfo(friendKey);
        updateSelfAvatar2Friend(friend);
    }

    /**
     * updata my avatar to custom friend
     */
    public void updateSelfAvatar2All() {
        List<ContactInfo> list = State.infoRepo().getUnsendAvatarFriend();
        for (ContactInfo friend : list) {
            updateSelfAvatar2Friend(friend);
        }
    }

    private void updateSelfAvatar2Friend(ContactInfo friend) {
        if (friend.isOnline()) {
            File avatarFile =
                FileKind.AVATAR.getFile(coreManager.toxBase.getSelfKey().key + ".png");
            State.infoRepo().delAvatarMessage(friend.getKey().key);
            if (avatarFile != null && avatarFile.exists()) {
                byte[] hashBytes = coreManager.toxBase.hashFile(avatarFile);
                sendFileSendRequest(avatarFile.getPath(), friend.getKey(), FileKind.AVATAR,
                    ToxFileId.unsafeFromValue(hashBytes));
            } else {
                sendFileDelRequest(friend.getKey(), FileKind.AVATAR);
            }
        }
    }

    private ToxFileId reInitFileId(String path, ToxFileId fileId) {
        try {
            if (fileId == null) {
                String hashStr = FileUtilsJ.getMd5Hash(path + "-" + new File(path).length());
                if (StringUtils.isEmpty(hashStr)) {
                    fileId = ToxFileId.empty();
                } else {
                    fileId = ToxFileId.unsafeFromValue(hashStr.getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }

    public void sendFileSendRequest(String path, ContactsKey key, FileKind fileKind,
        ToxFileId fileId) {
        String fileName = FileUtilsJ.getFileName(path);
        File file = new File(path);
        long length = file.length();
        fileId = reInitFileId(path, fileId);
        addSendFileInfo(key, key, path, fileKind, fileId, fileName.getBytes(), file, length);
    }

    /**
     * new send file method
     *
     * @param path file path(full path)
     * @param receiverKey receiver friend key
     * @param chatType chat type(friend or group)
     * @param isOnline is friend online
     * @param fileKind file type
     * @param fileId file id(has)
     */
    public void sendFileSendRequestNew(String path, ContactsKey receiverKey, String chatType,
        boolean isOnline, FileKind fileKind, ToxFileId fileId) {
        String fileName = FileUtilsJ.getFileName(path);
        File file = new File(path);
        long length = file.length();
        fileId = reInitFileId(path, fileId);

        /**
         * init send message info
         * 1.friend + online
         * 2.friend + offline
         * 3.group
         */
        ContactsKey proxyReceiverKey = receiverKey;
        byte[] transFileName = fileName.getBytes();

        addSendFileInfo(receiverKey, proxyReceiverKey, path, fileKind, fileId, transFileName, file,
            length);
    }

    private void addSendFileInfo(ContactsKey receiverKey, ContactsKey proxyReceiverKey, String path,
        FileKind fileKind, ToxFileId fileId, byte[] fileInfo, File file, long length) {
        int fileNumber =
            coreManager.toxBase.fileSend(proxyReceiverKey, fileKind.getKindId(), length, fileId,
                ToxFilename.unsafeFromValue(fileInfo));
        if (fileNumber > ToxCoreBase.SEND_MSG_FAIL) {
            long dbId = State.infoRepo()
                .addFileTransfer(fileNumber, receiverKey, coreManager.toxBase.getSelfKey(),
                    coreManager.toxBase.getName(), path, 0, GlobalParams.SEND_ING,
                    GlobalParams.RECEIVE_ING, true, length, fileKind);
            State.transfers.add(
                new FileTransfer(proxyReceiverKey, receiverKey, file, fileNumber, length, 0, true,
                    FileStatus.REQUEST_SENT, dbId, fileKind, false));
        }
    }

    public void sendFileDelRequest(ContactsKey key, FileKind fileKind) {
        coreManager.toxBase.fileSend(key, fileKind.getKindId(), 0, null,
            ToxFilename.unsafeFromValue("".getBytes()));
        if (fileKind.getKindId() == FileKind.AVATAR.getKindId()) {
            onSelfAvatarSendFinished(key);
        }
    }

    private void onSelfAvatarSendFinished(ContactsKey sentTo) {
        State.infoRepo().updateContactReceivedAvatar(sentTo, true);
    }

    public void sendFileDeleteRequest(ContactsKey key, FileKind fileKind) {
        coreManager.toxBase.fileSend(key, fileKind.getKindId(), 0, ToxFileId.empty(),
            ToxFilename.unsafeFromValue("".getBytes()));
        if (fileKind.getKindId() == FileKind.AVATAR.getKindId()) {
            onSelfAvatarSendFinished(key);
        }
    }

    public void acceptFile(ContactsKey friendKey, ContactsKey realReceiverKey, int fileNumber) {
        fileAcceptOrReject(friendKey, realReceiverKey, fileNumber, true);
    }

    public void rejectFile(ContactsKey friendKey, ContactsKey realReceiverKey, int fileNumber) {
        fileAcceptOrReject(friendKey, realReceiverKey, fileNumber, false);
    }

    public void fileFinished(ContactsKey proxyKey, int fileNumber) {
        FileTransfer transfer = State.transferManager().get(proxyKey, fileNumber);
        if (transfer != null) {
            ContactsKey realReceiverKey = transfer.getRealReceiverKey();
            transfer.setStatus(FileStatus.FINISHED);
            State.infoRepo().fileTransferFinish(realReceiverKey, fileNumber);
            State.infoRepo().clearFileNumber(realReceiverKey, fileNumber);
            if (transfer.getFileKind().getKindId() == FileKind.AVATAR.getKindId()) {
                if (transfer.isSending()) {
                    onSelfAvatarSendFinished(realReceiverKey);
                } else {
                    String realAvatarName = transfer.getFile().getName().replace("temp", "");
                    State.infoRepo().updateFriendAvatar(realReceiverKey, realAvatarName);
                    FileUtilsJ.delFile(StorageUtil.getAvatarsFolder() + realAvatarName);
                    FileUtilsJ.rename(transfer.getFile().getAbsolutePath(), realAvatarName);
                }
            }
        } else {
            LogUtil.i(TAG, "fileFinished: No transfer found ");
        }
    }

    private void fileAcceptOrReject(ContactsKey proxyKey, ContactsKey realReceiverKey,
        int fileNumber, boolean accept) {
        long dbId = State.infoRepo().getFileId(realReceiverKey, fileNumber);
        if (dbId != -1) {
            coreManager.toxBase.fileControl(proxyKey, fileNumber,
                (accept ? ToxFileControl.RESUME : ToxFileControl.CANCEL));
            if (accept) {
                State.infoRepo().fileTransferStarted(realReceiverKey.getKey(), fileNumber);
            } else {
                State.infoRepo().clearFileNumber(realReceiverKey, fileNumber);
            }
            FileTransfer transfer = State.transferManager().get(dbId);
            if (transfer != null) {
                if (accept) {
                    transfer.setStatus(FileStatus.IN_PROGRESS);
                } else {
                    transfer.setStatus(FileStatus.CANCELLED);
                }
            }
        }
    }

    public void cancelFile(ContactsKey key, int fileNumber) {
        LogUtil.i(TAG, "cancelFile");
        State.transfers.remove(key, fileNumber);
        State.infoRepo().clearFileNumber(key, fileNumber);
    }

    public void failFile(ContactsKey key, int fileNumber) {
        LogUtil.i(TAG, "failFile");
        State.transfers.remove(key, fileNumber);
        State.infoRepo().fileTransferFailed(key, fileNumber);
        State.infoRepo().clearFileNumber(key, fileNumber);
    }

    public long getProgress(long id) {
        FileTransfer transfer = State.transferManager().get(id);
        if (transfer != null) {
            return transfer.getProgress();
        }
        return 0L;
    }

    public void fileTransferStarted(ContactsKey key, int fileNumber) {
        LogUtil.i(TAG, "fileTransferStarted");
        State.infoRepo().fileTransferStarted(key.getKey(), fileNumber);
    }

    public void pauseFile(long id) {
        LogUtil.i(TAG, "pauseFile");
        FileTransfer transfer = State.transfers.get(id);
        if (transfer != null) {
            transfer.setStatus(FileStatus.PAUSED);
        }
    }

    public void receiveFileData(ContactsKey key, int fileNumber, byte[] data) {
        FileTransfer transfer = State.transferManager().get(key, fileNumber);
        if (transfer != null) {
            transfer.writeData(data);
        }
    }

    public boolean isTransferring() {
        //check transferMap has sendind file TODO
        return true;
    }

    public int interval() {
        return isTransferring() ? Intervals.WORKING.getInterval() : Intervals.AWAKE.getInterval();
    }

    public void add(FileTransfer transfer) {
        transferMap.put(transfer.getDbId(), transfer);
        keyAndFileId2DbId.put(transfer.getKey().key + transfer.getFileNumber(), transfer.getDbId());
    }

    public FileTransfer get(long id) {
        return transferMap.get(id);
    }

    public FileTransfer get(ContactsKey key, int fileNumber) {
        Long id = keyAndFileId2DbId.get(key.key + fileNumber);
        if (id != null) {
            return get(id);
        }
        return null;
    }

    public void remove(long id) {
        FileTransfer transfer = get(id);
        if (transfer != null) {
            transferMap.remove(id);
            keyAndFileId2DbId.remove(transfer.getKey().key + transfer.getFileNumber());
        }
    }

    public void remove(ContactsKey key, int fileNumber) {
        Long id = keyAndFileId2DbId.get(key.key + fileNumber);
        if (id != null) {
            remove(id);
        }
    }

    //TODO
    //def getProgressSinceXAgo(id: Long, ms: Long): Option[(Long, Long)] = {
    //    val mTransfer = State.transfers.get(id)
    //    mTransfer match {
    //        case Some(t) => t.getProgressSinceXAgo(ms)
    //        case None => None
    //    }
    //}
}
