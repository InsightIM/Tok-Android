package com.client.tok.ui.offlinecore;

import com.client.tok.bean.ToxAddress;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.MsgHelper;

public class OfflineSender {
    public static void sendQueryFriend(String pk) {
        String offlineProxyPk = new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey().getKey();
        MsgHelper.sendOfflineCmd(offlineProxyPk, OfflineBuilder.queryFriend(pk));
    }

    static void sendOfflineReq() {
        String offlineProxyPk = new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey().getKey();
        MsgHelper.sendOfflineCmd(offlineProxyPk, OfflineBuilder.offlineMsgPullReq());
    }

    static void sendDelReq(long msgId) {
        String offlineProxyPk = new ToxAddress(GlobalParams.OFFLINE_BOT_TOK_ID).getKey().getKey();
        MsgHelper.sendOfflineCmd(offlineProxyPk, OfflineBuilder.offlineDelReq(msgId));
    }
}
