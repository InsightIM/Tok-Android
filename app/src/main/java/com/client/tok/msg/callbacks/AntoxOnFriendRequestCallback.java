package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactsKey;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.State;
import com.client.tok.utils.LocalBroaderUtils;
import com.client.tok.utils.PreferenceUtils;
import im.tox.tox4j.core.data.ToxFriendRequestMessage;
import im.tox.tox4j.core.data.ToxPublicKey;

public class AntoxOnFriendRequestCallback {
    public void friendRequest(ToxPublicKey publicKey, int timeDelta,
        ToxFriendRequestMessage message) {
        InfoRepository inforRepo = State.infoRepo();
        ContactsKey key = new ContactsKey(publicKey.toHexString());
        if (!inforRepo.isContactBlocked(key.key)) {
            inforRepo.addFriendRequest(key, new String(message.value));
        }
    }
}
