package com.client.tok.tox;

import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.ToxAddress;
import com.client.tok.constant.MessageType;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.ui.chat2.timer.MsgTimer;
import com.client.tok.ui.offlinecore.OfflineBuilder;
import com.client.tok.utils.ByteUtil;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import im.tox.proto.Core;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxNickname;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

import static com.client.tok.tox.ToxCoreBase.SEND_MSG_FAIL;

public class MsgHelper {
    private static String TAG = "MsgHelper";

    public static void receiveMessage(ContactInfo friendInfo, String message,
        MessageType messageType, long messageId, long timeStamp) {
        InfoRepository infoRepo = State.infoRepo();
        //black list:not receive message
        String senderKey = friendInfo.getKey().key;
        if (!infoRepo.isContactBlocked(senderKey)) {
            boolean chatActive = State.isChatActive(senderKey);
            String senderName = friendInfo.getDisplayName();
            infoRepo.addMessage2(friendInfo.getKey(), friendInfo.getKey(),
                ToxNickname.unsafeFromValue(senderName.getBytes()), message, timeStamp,
                GlobalParams.SEND_SUCCESS, chatActive, messageType, messageId);

            if (!chatActive) {
                int unreadCount = infoRepo.totalUnreadCount();
                NotifyManager.getInstance()
                    .createMsgNotify(GlobalParams.CHAT_FRIEND, senderKey, senderName, message,
                        unreadCount);
            }
        }
    }

    public static void sendFriendMessage(ContactsKey receiverKey, boolean onLine, String msg,
        MessageType msgType) {
        sendMessage(receiverKey, onLine, msg, msgType, true);
    }

    public static void sendGroupMessage(ContactsKey groupKey, String msg, MessageType msgType) {
        sendMessage(groupKey, false, msg, msgType, false);
    }

    private static void sendMessage(final ContactsKey receiverKey, final boolean onLine,
        final String msg, final MessageType msgType, final boolean isFriend) {
        final ToxManager manager = ToxManager.getManager();
        final InfoRepository infoRepo = State.infoRepo();
        for (final String splitMsg : splitMessage(msg)) {
            ContactsKey senderKey = manager.toxBase.getSelfKey();
            ToxNickname senderName = manager.toxBase.getName();
            final long dbId = infoRepo.addMessage(receiverKey, senderKey, senderName, splitMsg,
                GlobalParams.SEND_ING, true, msgType, -2);//can't -1
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    long id = 0;
                    if (isFriend) {
                        if (onLine) {
                            id = manager.toxBase.friendSendMessage(receiverKey,
                                ToxFriendMessage.unsafeFromValue(splitMsg.getBytes()),
                                MessageType.toToxMessageType(msgType));
                        } else {
                            id = manager.toxBase.generateUniqueId(receiverKey);
                            ContactsKey botKey =
                                new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey();
                            //ignore the id
                            LogUtil.i(TAG, "botKey:" + botKey.getKey() + ",offlineMsgId:" + id);
                            manager.toxBase.friendSendMessageOffline(botKey,
                                OfflineBuilder.offlineMsgSend(id, receiverKey.getKey(), splitMsg));
                        }
                    }

                    if (id > 0) {
                        infoRepo.setMessageSending(id, dbId);
                        MsgTimer.startTimer(id);
                    } else {
                        infoRepo.setMessageFailByDbId(dbId);
                    }
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    public static int sendBackgroundCmd(String receiveKey, byte[] msgBytes, MessageType msgType) {
        if (!StringUtils.isEmpty(receiveKey) && msgBytes != null && msgBytes.length > 0) {
            final ToxManager manager = ToxManager.getManager();
            try {
                return manager.toxBase.friendSendMessage(new ContactsKey(receiveKey),
                    ToxFriendMessage.unsafeFromValue(msgBytes),
                    MessageType.toToxMessageType(msgType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SEND_MSG_FAIL;
    }

    public static int sendOfflineCmd(String receiveKey, Core.FriendMessageOffline offlineInfo) {
        if (!StringUtils.isEmpty(receiveKey) && offlineInfo != null) {
            final ToxManager manager = ToxManager.getManager();
            try {
                return manager.toxBase.friendSendMessageOffline(new ContactsKey(receiveKey),
                    offlineInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SEND_MSG_FAIL;
    }

    private static List<String> splitMessage(String msg) {
        List<String> result = new ArrayList<>();
        try {
            int currSplitPos = 0;
            byte[] msgBytes = msg.getBytes("UTF-8");

            while (msgBytes.length - currSplitPos > GlobalParams.MAX_MESSAGE_LENGTH) {
                int pos = currSplitPos + GlobalParams.MAX_MESSAGE_LENGTH;
                while ((msgBytes[pos] & 0xc0) == 0x80) {
                    pos--;
                }
                String str = new String(ByteUtil.byteSplit(msgBytes, currSplitPos, pos));
                result.add(str);
                currSplitPos = pos;
            }

            if (msgBytes.length - currSplitPos > 0) {
                result.add(new String(ByteUtil.byteSplit(msgBytes, currSplitPos)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
