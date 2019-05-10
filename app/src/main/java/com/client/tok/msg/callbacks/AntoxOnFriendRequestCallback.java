package com.client.tok.msg.callbacks;

import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.tox.State;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxPublicKey;

public class AntoxOnFriendRequestCallback {
    public void friendRequest(ToxPublicKey publicKey, int timeDelta,
        ToxFriendRequestMessage message) {
        InfoRepository infoRepo = State.infoRepo();
        String key = publicKey.toHexString();
        if (!infoRepo.isContactBlocked(key)) {
            String reqMsg = new String(message.value);
            infoRepo.addFriendRequest(key, reqMsg);
            NotifyManager.getInstance()
                .createFriendReqNotify(key, reqMsg, infoRepo.totalUnreadCount());
        }
    }
}
