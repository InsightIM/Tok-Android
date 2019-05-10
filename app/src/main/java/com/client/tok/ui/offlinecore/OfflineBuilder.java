package com.client.tok.ui.offlinecore;

import com.client.tok.bean.ContactsKey;
import com.client.tok.tox.ToxManager;
import com.client.tok.utils.LogUtil;
import com.client.tok.utils.StringUtils;
import com.google.protobuf.ByteString;
import im.tox.proto.Core;
import im.tox.proto.Offline;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;

public class OfflineBuilder {
    private static String TAG = "OfflineBuilder";

    public static Core.FriendMessageOffline queryFriend(String pk) {
        Core.FriendMessageOffline.Builder infoBuilder = Core.FriendMessageOffline.newBuilder();
        infoBuilder.setCmd(OfflineCmd.TOX_MESSAGE_OFFLINE_QUERY_FRIEND_REQUEST.getType());

        Offline.QueryFriendReq.Builder queryBuilder = Offline.QueryFriendReq.newBuilder();
        queryBuilder.setPk(ByteString.copyFrom(StringUtils.getBytes(pk)));

        infoBuilder.setMessage(queryBuilder.build().toByteString());
        LogUtil.i(TAG, "queryFriend:" + infoBuilder.build().toString());

        return infoBuilder.build();
    }

    /**
     * build offline msg
     *
     * @param msgId as local_msg_id in pb,must be unique to current user,can be dbId or general by jni(time million+random)
     * @param toPk real receiver pk
     * @param msg content
     * @return byte
     */
    public static Core.FriendMessageOffline offlineMsgSend(long msgId, String toPk, String msg) {
        try {
            Core.FriendMessageOffline.Builder infoBuilder = Core.FriendMessageOffline.newBuilder();
            infoBuilder.setCmd(OfflineCmd.TOX_MESSAGE_OFFLINE_SEND_REQUEST.getType());

            Offline.OfflineMessageReq.Builder reqBuilder = Offline.OfflineMessageReq.newBuilder();
            reqBuilder.setLocalMsgId(msgId);
            reqBuilder.setToPk(ByteString.copyFrom(StringUtils.getBytes(toPk)));

            ToxManager manager = ToxManager.getManager();
            int friendNumber = manager.toxBase.getFriendNumber(new ContactsKey(toPk)).value;
            byte[] encryptMsg = manager.toxBase.encryptMsg(friendNumber, StringUtils.getBytes(msg));
            reqBuilder.setCryptoMessage(ByteString.copyFrom(encryptMsg));

            infoBuilder.setMessage(reqBuilder.build().toByteString());
            LogUtil.i(TAG, "offlineMsgSend:" + infoBuilder.build().toString());

            return infoBuilder.build();
        } catch (ToxFriendByPublicKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Core.FriendMessageOffline offlineMsgPullReq() {
        Core.FriendMessageOffline.Builder infoBuilder = Core.FriendMessageOffline.newBuilder();
        infoBuilder.setCmd(OfflineCmd.TOX_MESSAGE_OFFLINE_PULL_REQUEST.getType());

        LogUtil.i(TAG, "offlineMsgPullReq:" + infoBuilder.build().toString());
        return infoBuilder.build();
    }

    public static Core.FriendMessageOffline offlineDelReq(long lastMsgId) {
        Core.FriendMessageOffline.Builder infoBuilder = Core.FriendMessageOffline.newBuilder();
        infoBuilder.setCmd(OfflineCmd.TOX_MESSAGE_OFFLINE_DEL_REQUEST.getType());

        Offline.OfflineMessageDelReq.Builder delBuilder = Offline.OfflineMessageDelReq.newBuilder();
        delBuilder.setLastMsgId(lastMsgId);

        infoBuilder.setMessage(delBuilder.build().toByteString());
        LogUtil.i(TAG, "offlineDelReq:" + infoBuilder.build().toString());

        return infoBuilder.build();
    }
}
