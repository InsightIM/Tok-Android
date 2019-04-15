package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactsInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import im.tox.tox4j.core.enums.ToxUserStatus;

public class AntoxOnUserStatusCallback {
    public void friendStatus(ContactsInfo friendInfo, ToxUserStatus status) {
        InfoRepository infoRepo = State.infoRepo();
        infoRepo.updateContactStatus(friendInfo.getKey(), status);
    }
}
