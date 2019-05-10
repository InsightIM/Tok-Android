package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.constant.FileKind;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.rx.RxBus;
import com.client.tok.rx.event.PortraitEvent;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.ByteUtil;
import com.client.tok.utils.FileUtilsJ;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StorageUtil;
import im.tox.tox4j.core.data.ToxFilename;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxFileControl;
import java.io.File;

public class AntoxOnFileRecvCallback {

    private String TAG = "AntoxOnFileRecvCallback";

    public void fileRecv(ContactInfo friendInfo, int fileNumber, int toxFileKind, long fileSize,
        ToxFilename filename) {
        FileKind kind = FileKind.fromKindId(toxFileKind);

        if (kind.getKindId() == FileKind.AVATAR.getKindId()) {
            //file is avatar
            String name = friendInfo.getKey().getKey() + ".png"; //TODO full path
            if (fileSize > GlobalParams.MAX_AVATAR_SIZE) {
                return;
            } else if (fileSize == 0) {
                ToxManager.getManager().toxBase.fileControl(friendInfo.getKey(), fileNumber,
                    ToxFileControl.CANCEL);

                InfoRepository infoRepo = State.infoRepo();
                // friendInfo.avatar().foreach(_.delete());
                infoRepo.updateFriendAvatar(friendInfo.getKey(), "");
                FileUtilsJ.delFile(StorageUtil.getAvatarsFolder() + name);
                RxBus.publish(new PortraitEvent(name, ""));
                return;
            }

            String fileId =
                ToxManager.getManager().toxBase.fileGetFileId(friendInfo.getKey(), fileNumber)
                    .toHexString();
            File avatarFile = FileKind.AVATAR.getFile(name);
            LogUtil.i(TAG, "friendKey:" + friendInfo.getKey());

            if (avatarFile != null) {
                String storedFileId = ByteUtil.bytes2HexStr(
                    ToxManager.getManager().toxBase.hash(FileUtilsJ.readToBytes(avatarFile)));
                LogUtil.i(TAG, "fileId:" + fileId + ",storedFiledId:" + storedFileId);
                if (fileId.equals(storedFileId)) {
                    LogUtil.i(TAG, "friend avatar cancel:" + friendInfo.getKey().getKey());
                    ToxManager.getManager().toxBase.fileControl(friendInfo.getKey(), fileNumber,
                        ToxFileControl.CANCEL);
                    return;
                }
            }
            LogUtil.i(TAG, "avatar,key:"
                + friendInfo.getKey().getKey()
                + ",avatarFile:"
                + avatarFile
                + ",fileSize:"
                + fileSize
                + ",fileNumber:"
                + fileNumber);
            //delete old avater message
            State.infoRepo().delAvatarMessage(friendInfo.getKey().getKey());
            //if avatar is in downloading,don't delete the old avatar,if new avatar download failed,it display error
            //so when download successful,rename new avatar as the old avatar
            String nameTemp = name + "temp";
            String avatarPath = StorageUtil.getAvatarsFolder() + nameTemp;
            State.transferManager()
                .avatarIncomingRequest(friendInfo.getKey(), friendInfo.getName(), true, fileNumber,
                    avatarPath, kind, fileSize, kind.isReplaceExisted());
            if (kind.isAutoAccept()) {
                State.transferManager()
                    .acceptFile(friendInfo.getKey(), friendInfo.getKey(), fileNumber);
            }
        } else {
            //好友或群组传输文件
            String filePath = StorageUtil.getFilesFolder() + filename; // full path
            ToxNickname senderName =
                ToxNickname.unsafeFromValue(friendInfo.getDisplayName().getBytes());

            ContactsKey realReceiverKey = friendInfo.getKey();
            ContactsKey proxyContactsKey = friendInfo.getKey();
            ContactsKey realSenderKey = friendInfo.getKey();
            long createTime = System.currentTimeMillis(); //bot forward,when offline/group message

            LogUtil.i(TAG, "receiverFileInfo "
                + "realReceiverKey:"
                + realReceiverKey.getKey()
                + ",proxyKey:"
                + proxyContactsKey.getKey()
                + ",realSenderKey:"
                + realSenderKey.getKey()
                + ",senderName:"
                + senderName);
            boolean chatActive = State.isChatActive(realReceiverKey.key);
            State.transferManager()
                .fileIncomingRequest(realReceiverKey, proxyContactsKey, realSenderKey, senderName,
                    chatActive, fileNumber, filePath, kind, fileSize, createTime,
                    kind.isReplaceExisted());
            if (kind.isAutoAccept()) {
                State.transferManager().acceptFile(proxyContactsKey, realReceiverKey, fileNumber);
            }
        }
    }
}
