package com.client.tok.db.repository;

import android.arch.lifecycle.LiveData;
import android.graphics.Rect;
import com.client.tok.TokApplication;
import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.Conversation;
import com.client.tok.bean.ConversationItem;
import com.client.tok.bean.FriendRequest;
import com.client.tok.bean.Message;
import com.client.tok.constant.ContactType;
import com.client.tok.constant.FileKind;
import com.client.tok.constant.MessageType;
import com.client.tok.db.DBConstants;
import com.client.tok.db.info.ContactsDao;
import com.client.tok.db.info.ConversationDao;
import com.client.tok.db.info.FriendReqDao;
import com.client.tok.db.info.InfoDB;
import com.client.tok.db.info.MsgDao;
import com.client.tok.msg.UserStatus;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.ui.imgzoom.ImgViewInfo;
import com.client.tok.ui.imgzoom.ImgViewInfoList;
import com.client.tok.utils.ImageUtils;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.data.ToxStatusMessage;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import java.util.ArrayList;
import java.util.List;

public class InfoRepository {
    public InfoDB infoDB;
    private ConversationDao conversationDao;
    private MsgDao msgDao;
    private ContactsDao contactsDao;
    private FriendReqDao friendReqDao;

    public InfoRepository() {
        infoDB = InfoDB.getInstance(TokApplication.getInstance());
        conversationDao = infoDB.conversationDao();
        msgDao = infoDB.messageDao();
        contactsDao = infoDB.contactsDao();
        friendReqDao = infoDB.friendReqDao();
    }

    public void synchroniseWithTox(List<ContactsKey> list) {
        if (list != null && list.size() > 0) {
            for (ContactsKey key : list) {
                if (!doesContactExist(key.key)) {
                    addFriend(key, "", "", "");
                }
            }
        }
    }

    public long addConversation(String key) {
        return updateConversation(key, -1);
    }

    /**
     * insert or update
     */
    private long updateConversation(String key, long lastMsgDbId) {
        if (lastMsgDbId <= 0) {
            Message msg = getLastMessage(key);
            if (msg != null) {
                lastMsgDbId = msg.getId();
            }
        }
        Conversation conversation = conversationDao.getConversationByKey(key);
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setKey(key);
            conversation.setLastMsgId(lastMsgDbId);
            conversation.setUnreadCount(1000);
            conversation.setUpdateTime(System.currentTimeMillis());
            return conversationDao.insert(conversation);
        } else {
            conversation.setLastMsgId(lastMsgDbId);
            conversation.setUpdateTime(System.currentTimeMillis());
            conversation.setUnreadCount(1000);
            return conversationDao.update(conversation);
        }
    }

    public int delConversation(String key) {
        return conversationDao.delByKey(key);
    }

    public boolean doesContactExist(String key) {
        return contactsDao.countByKey(key) > 0;
    }

    public LiveData<List<ConversationItem>> conversationLive() {
        return conversationDao.getConversationLive();
    }

    public long addFriend(ContactsKey key, String name, String alias, String statusMsg) {
        ContactsInfo contacts =
            new ContactsInfo(key, name, alias, statusMsg, ContactType.FRIEND.getType());
        return contactsDao.insert(contacts);
    }

    private long addToMessagesTable(int messageId, ContactsKey key, ContactsKey senderKey,
        ToxNickname senderName, String message, long createTime, int sentStatus, int receiverStatus,
        boolean hasBeenRead, long size, MessageType messageType, FileKind fileKind) {
        createTime = createTime > 0 ? createTime : System.currentTimeMillis();
        Message msg =
            new Message(0, messageId, key, senderKey, senderName.toString(), message, sentStatus,
                receiverStatus, hasBeenRead, false, createTime, size, messageType, fileKind);
        long dbId = msgDao.insert(msg);
        //update conversation
        if (fileKind != FileKind.AVATAR) {
            updateConversation(key.getKey(), dbId);
        }
        return dbId;
    }

    public long addFileTransfer(int fileNumber, ContactsKey key, ContactsKey senderKey,
        ToxNickname senderName, String path, long createTime, int sentStatus, int receiverStatus,
        boolean hasBeenRead, long size, FileKind fileKind) {
        return addToMessagesTable(fileNumber, key, senderKey, senderName, path, createTime,
            sentStatus, receiverStatus, hasBeenRead, size, MessageType.FILE_TRANSFER, fileKind);
    }

    public long addMessage2(ContactsKey key, ContactsKey senderKey, ToxNickname senderName,
        String message, long createTime, int sentStatus, boolean hasBeenRead,
        ToxMessageType messageType, int messageId) {
        return addToMessagesTable(messageId, key, senderKey, senderName, message, createTime,
            sentStatus, GlobalParams.RECEIVE_ING, hasBeenRead, 0,
            MessageType.fromToxMessageType(messageType), FileKind.INVALID);
    }

    public long addMessage(ContactsKey key, ContactsKey senderKey, ToxNickname senderName,
        String message, int sentStatus, boolean hasBeenRead, ToxMessageType messageType,
        int messageId) {
        return addMessage2(key, senderKey, senderName, message, 0, sentStatus, hasBeenRead,
            messageType, messageId);
    }

    public int fileTransferStarted(String key, int fileNumber) {
        return msgDao.updateSentStatus(key, fileNumber, GlobalParams.SEND_ING);
    }

    public LiveData<Integer> totalUnreadMsg() {
        return msgDao.totalUnread();
    }

    public int totalUnreadCount() {
        return msgDao.totalUnreadCount();
    }

    public int totalUnreadCount(String key) {
        return msgDao.totalUnreadCount(key);
    }

    public int getFileId(ContactsKey key, int fileNumber) {
        return msgDao.getFileId(key.toString(), fileNumber);
    }

    public int clearAllFileNumbers() {
        return msgDao.clearFileNumbers();
    }

    public int clearFileNumber(ContactsKey key, int fileNumber) {
        return msgDao.clearFileNumbers(key.toString(), fileNumber);
    }

    public int fileTransferFailed(ContactsKey key, int fileNumber) {
        return msgDao.setFileStatus(key.toString(), fileNumber, GlobalParams.SEND_FAIL,
            GlobalParams.RECEIVE_FAIL);
    }

    public int fileTransferFinish(ContactsKey key, int fileNumber) {
        return msgDao.setFileStatus(key.toString(), fileNumber, GlobalParams.SEND_SUCCESS,
            GlobalParams.RECEIVE_SUCCESS);
    }

    public long addFriendRequest(ContactsKey key, String reqMsg) {
        FriendRequest request = friendReqDao.queryByKey(key.key);
        if (request != null) {
            request.setRequestMessage(reqMsg);
            //request.setHasRead(false); not agree,and tox will send some times,so no change this value
            return friendReqDao.update(request);
        } else {
            request = new FriendRequest(key, reqMsg);
            return friendReqDao.insert(request);
        }
    }

    public long delFriendRequest(String key) {
        return friendReqDao.delFriendReq(key);
    }

    public LiveData<List<FriendRequest>> friendReqLive() {
        return friendReqDao.getAllObserver();
    }

    public FriendRequest getFriendReq(String key) {
        return friendReqDao.queryByKey(key);
    }

    public int setFriendReqRead() {
        return friendReqDao.setHasRead();
    }

    public LiveData<Integer> getFriendReqUnReadCount() {
        return friendReqDao.getUnReadCount();
    }

    public List<Message> getUnsentMessageList(ContactsKey ContactsKey) {
        return msgDao.getUnsentMsgList(ContactsKey.toString());
    }

    public int setMessageFailByReceiptId(int receiptId) {
        return msgDao.setMsgFailByMsgId(receiptId);
    }

    public int setMessageFailByDbId(long dbId) {
        return msgDao.setMsgFailByDbId(dbId);
    }

    public int setMessageSending(int messageId, long dbId) {
        return msgDao.setMsgSending(messageId, dbId);
    }

    public int setMessageReceived(int receiptId) {
        return msgDao.setMessageReceived(receiptId);
    }

    public int markReaded(String key) {
        //clear conversation unread count tag
        conversationDao.clearUnreadTag(key);
        return msgDao.markReaded(key);
    }

    public int deleteMessage(long dbId) {
        Message msg = msgDao.getMessageByDbId(dbId);
        String key = msg.getKey().getKey();
        int result = msgDao.delMsgByDbId(dbId);
        Message lastMsg = getLastMessage(key);
        if (lastMsg != null) {
            updateConversation(key, lastMsg.getId());
        }
        return result;
    }

    public int deleteConversation(String key) {
        return conversationDao.delByKey(key);
    }

    public int deleteMessage(String key) {
        int result = msgDao.delMsgByKey(key);
        updateConversation(key, -1);
        return result;
    }

    public int delAvatarMessage(String key) {
        return msgDao.delAvatarMessage(key);
    }

    public Message getLastMessage(String key) {
        return msgDao.getLastMessage(key);
    }

    public LiveData<List<ContactsInfo>> friendList() {
        return contactsDao.contactsListObserver(ContactType.FRIEND.getType());
    }

    public LiveData<List<ContactsInfo>> groupList() {
        return contactsDao.contactsListObserver(ContactType.GROUP.getType());
    }

    public int setAllOffline() {
        return contactsDao.setAllOffline();
    }

    public int delContactByKey(String key) {
        return contactsDao.delContactByKey(key);
    }

    public int updateContact(String friendKey, String key, Object value) {
        ContactsInfo info = contactsDao.contactsInfo(friendKey);
        if (info != null) {
            switch (key) {
                case DBConstants.COLUMN_NOTE:
                    info.setSignature(value.toString());
                    break;
                case DBConstants.COLUMN_STATUS:
                    info.setStatus(value.toString());
                    break;
                case DBConstants.COLUMN_ISONLINE:
                    info.setOnline((Boolean) value);
                    break;
                case DBConstants.COLUMN_AVATAR:
                    info.setAvatar(value.toString());
                    break;
                case DBConstants.COLUMN_RECEIVED_AVATAR:
                    info.setReceivedAvatar((Boolean) value);
                    break;
                case DBConstants.COLUMN_ALIAS:
                    info.setAlias(ToxNickname.unsafeFromValue(((String) value).getBytes()));
                    break;
                case DBConstants.COLUMN_NAME:
                    info.setName(ToxNickname.unsafeFromValue(((String) value).getBytes()));
                    break;
            }
            return contactsDao.update(info);
        } else {
            return 0;
        }
    }

    public int updateContactName(ContactsKey key, String newName) {
        return updateContact(key.key, DBConstants.COLUMN_NAME, newName);
    }

    public int updateContactStatusMessage(ContactsKey key, ToxStatusMessage statusMessage) {
        return updateContact(key.key, DBConstants.COLUMN_NOTE, new String(statusMessage.value));
    }

    public int updateContactStatus(ContactsKey key, ToxUserStatus status) {
        return updateContact(key.key, DBConstants.COLUMN_STATUS,
            UserStatus.getStringFromUserStatus(status));
    }

    public int updateContactOnline(ContactsKey key, boolean online) {
        return updateContact(key.key, DBConstants.COLUMN_ISONLINE, online);
    }

    public int updateFriendAvatar(ContactsKey key, String avatar) {
        return updateContact(key.key, DBConstants.COLUMN_AVATAR, avatar);
    }

    public int updateContactReceivedAvatar(ContactsKey key, boolean receivedAvatar) {
        return updateContact(key.key, DBConstants.COLUMN_RECEIVED_AVATAR, receivedAvatar);
    }

    public int updateAlias(String contactKey, String alias) {
        return updateContact(contactKey, DBConstants.COLUMN_ALIAS, alias);
    }

    public int setAllFriendReceivedAvatar(boolean receivedAvatar) {
        return contactsDao.setAllFriendReceivedAvatar(receivedAvatar);
    }

    public Message getDraftMessage(String key) {
        return msgDao.queryMsgByKeyAndType(key, MessageType.DRAFT.getType());
    }

    public LiveData<List<Message>> getMessageByKeyLive(String key) {
        return msgDao.getAllMsgLive(key);
    }

    public ContactsInfo getFriendInfo(String key) {
        return contactsDao.contactsInfo(key);
    }

    public LiveData<ContactsInfo> getFriendInfoLive(String key) {
        return contactsDao.friendInfoObserver(key);
    }

    public List<ContactsInfo> getUnsendAvatarFriend() {
        return contactsDao.getUnsendAvatarFriendList();
    }

    public int deleteAllMessage() {
        return msgDao.delAll();
    }

    public void deleteAll() {
        msgDao.delAll();
        conversationDao.delAll();
        contactsDao.delAll();
    }

    public int setContactMute(String key, boolean mute) {
        return contactsDao.setContactMute(key, mute);
    }

    public int setHasPlayed(long dbId) {
        return msgDao.setHasPlayed(dbId, true);
    }

    public boolean isContactMute(String key) {
        ContactsInfo friendInfo = contactsDao.contactsInfo(key);
        return friendInfo != null && friendInfo.isMute();
    }

    public boolean isContactBlocked(String key) {
        ContactsInfo friendInfo = contactsDao.contactsInfo(key);
        return friendInfo != null && friendInfo.isBlocked();
    }

    public ImgViewInfoList getImgMessage(String key, String curPath, Rect rect) {
        ImgViewInfoList infoList = new ImgViewInfoList();
        infoList.setCurPath(curPath);
        List<ImgViewInfo> imgList = new ArrayList<>();
        List<Message> list = msgDao.getFileMessage(key);
        int curPathIndex = 0;
        if (list != null && list.size() > 0) {
            for (Message msg : list) {
                if (ImageUtils.isImgFile(msg.getMessage())) {
                    ImgViewInfo imgViewInfo = new ImgViewInfo();
                    imgViewInfo.setPath(msg.getMessage());
                    imgViewInfo.setBounds(rect);
                    imgList.add(imgViewInfo);
                    if (curPath != null && curPath.equals(msg.getMessage())) {
                        infoList.setCurIndex(curPathIndex);
                    }
                    curPathIndex += 1;
                }
            }
        }
        infoList.setImgViewInfoList(imgList);
        return infoList;
    }

    public void destroy() {
        if (infoDB != null) {
            infoDB.destroy();
            infoDB = null;
        }
    }
}
