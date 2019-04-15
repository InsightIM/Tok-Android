package com.client.tok.tox;

import com.client.tok.bean.ContactsInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.ui.chat2.timer.MsgTimer;
import com.client.tok.utils.ByteUtil;
import im.tox.tox4j.core.data.ToxFriendMessage;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxMessageType;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MsgHelper {
    private static String TAG = "MsgHelper";

    public static void receiveMessage(ContactsInfo friendInfo, ToxFriendMessage message,
        ToxMessageType messageType, long timeStamp) {
        InfoRepository infoRepo = State.infoRepo();
        //black list:not receive message
        if (!infoRepo.isContactBlocked(friendInfo.getKey().key)) {
            boolean chatActive = State.isChatActive(friendInfo.getKey().key);
            String msg = new String(message.value);
            infoRepo.addMessage2(friendInfo.getKey(), friendInfo.getKey(),
                ToxNickname.unsafeFromValue(friendInfo.getDisplayName().getBytes()), msg, timeStamp,
                GlobalParams.SEND_SUCCESS, chatActive, messageType, -1);

            if (!chatActive) {
                int unreadCount = infoRepo.totalUnreadCount();
                NotifyManager.getInstance()
                    .createMsgNotify(GlobalParams.CHAT_FRIEND, friendInfo, msg, unreadCount);
            }
        }
    }

    public static void sendFriendMessage(ContactsKey receiverKey, boolean onLine, String msg,
        ToxMessageType msgType) {
        sendMessage(receiverKey, onLine, msg, msgType, true);
    }

    public static void sendGroupMessage(ContactsKey groupKey, String msg, ToxMessageType msgType) {
        sendMessage(groupKey, false, msg, msgType, false);
    }

    private static void sendMessage(ContactsKey receiverKey, boolean onLine, String msg,
        ToxMessageType msgType, boolean isFriend) {
        CoreManager manager = CoreManager.getManager();
        InfoRepository infoRepo = State.infoRepo();
        for (String splitMsg : splitMessage(msg)) {
            ContactsKey senderKey = manager.toxBase.getSelfKey();
            ToxNickname senderName = manager.toxBase.getName();
            long dbId = infoRepo.addMessage(receiverKey, senderKey, senderName, splitMsg,
                GlobalParams.SEND_ING, true, msgType, -2);//can't -1
            Observable.create((ObservableEmitter<Object> emitter) -> {
                int id = 0;
                if (isFriend) {
                    id = manager.toxBase.friendSendMessage(receiverKey, onLine,
                        ToxFriendMessage.unsafeFromValue(splitMsg.getBytes()), msgType);
                }
                if (id > 0) {
                    infoRepo.setMessageSending(id, dbId);
                    MsgTimer.startTimer(id);
                } else {
                    infoRepo.setMessageFailByDbId(dbId);
                }
                emitter.onComplete();
            }).subscribeOn(Schedulers.io()).subscribe();
        }
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
