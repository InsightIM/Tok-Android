package com.client.tok.ui.offlinecore;

import com.client.tok.bean.ContactInfo;
import com.client.tok.bean.ContactsKey;
import com.client.tok.constant.MessageType;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.MsgHelper;
import com.client.tok.tox.State;
import com.client.tok.tox.ToxManager;
import com.client.tok.ui.chat2.timer.MsgTimer;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import im.tox.proto.Offline;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import java.util.List;

public class OfflineHandler {
    private static final String TAG = "OfflineHandler";

    public static void handle(int cmd, byte[] bytes) {
        try {
            OfflineCmd offlineCmd = OfflineCmd.get(cmd);
            if (offlineCmd != null) {
                LogUtil.i(TAG, "offline msg handler,cmd:" + offlineCmd.name());
                switch (offlineCmd) {
                    case TOX_MESSAGE_OFFLINE_QUERY_FRIEND_RESPONSE://response:check friend has add offline bot
                        queryFriendRes(Offline.QueryFriendRes.parseFrom(bytes));
                        break;
                    case TOX_MESSAGE_OFFLINE_SEND_RESPONSE://response:send offline message to offlinebot
                        offlineSendRes(Offline.OfflineMessageRes.parseFrom(bytes));
                        break;
                    case TOX_MESSAGE_OFFLINE_PULL_RESPONSE://response:get offline messages
                        offlinePullRes(Offline.OfflineMessagePullRes.parseFrom(bytes));
                        break;
                    case TOX_MESSAGE_OFFLINE_READ_NOTICE://notice: current user has offline message
                        offlineNotice(Offline.OfflineMessageReadNotice.parseFrom(bytes));
                        break;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void queryFriendRes(Offline.QueryFriendRes response) {
        if (response != null) {
            String pk = StringUtils.byte2Str(response.getPk()).toUpperCase();
            boolean hasOfflineBot = response.getExist() == 1;
            LogUtil.i(TAG, "queryFriendRes pk:" + pk + ",HasOfflineBot:" + hasOfflineBot);
            State.infoRepo().updateHasOfflineBot(pk, hasOfflineBot);
        }
    }

    private static void offlineSendRes(Offline.OfflineMessageRes response) {
        if (response != null) {
            long msgId = response.getLocalMsgId();
            LogUtil.i(TAG,"offline send Response, msgId:"+msgId);
            MsgTimer.stopTimer(msgId);
            State.infoRepo().setMessageReceived(msgId);
        }
    }

    private static void offlinePullRes(Offline.OfflineMessagePullRes response) {
        if (response != null) {
            List<Offline.OfflineMessage> list = response.getMsgList();
            if (list != null && list.size() > 0) {
                long msgId = 0L;
                for (Offline.OfflineMessage offlineMsg : list) {
                    InfoRepository infoRepo = State.infoRepo();
                    //get the pk of the message from where friend
                    String fromPk = StringUtils.byte2Str(offlineMsg.getFrPk());
                    long localMsgId = offlineMsg.getLocalMsgId();
                    //check if message has exist
                    if (!infoRepo.msgExist(fromPk, localMsgId)) {
                        try {
                            ToxManager manager = ToxManager.getManager();
                            int friendNumber =
                                manager.toxBase.getFriendNumber(new ContactsKey(fromPk)).value;
                            byte[] decryptMsg = manager.toxBase.decryptMsg(friendNumber,
                                offlineMsg.getContent().toByteArray());
                            String msg = StringUtils.byte2Str(decryptMsg);
                            LogUtil.i(TAG, "offline res,msg:" + msg + ",fromPk:" + fromPk);
                            long timestamp = offlineMsg.getCreateTime();
                            ContactInfo contactInfo = infoRepo.getFriendInfo(fromPk);
                            MsgHelper.receiveMessage(contactInfo, msg, MessageType.MESSAGE,
                                localMsgId, timestamp);
                        } catch (ToxFriendByPublicKeyException e) {
                            //TODO if crash,maybe the the looper finish,and send del request,the message will be lost
                            e.printStackTrace();
                            break;
                        }
                    }
                    msgId = offlineMsg.getMsgId();
                }
                //del the messages what we have received
                if (msgId > 0) {
                    OfflineSender.sendDelReq(msgId);
                }

                int leftCount = response.getLeftCount();
                if (leftCount > 0) {
                    //has not receive all offline messages, go on,resend offline message request
                    OfflineSender.sendOfflineReq();
                }
            }
        }
    }

    private static void offlineNotice(Offline.OfflineMessageReadNotice response) {
        if (response != null) {
            //send pull offline message request
            OfflineSender.sendOfflineReq();
        }
    }
}
