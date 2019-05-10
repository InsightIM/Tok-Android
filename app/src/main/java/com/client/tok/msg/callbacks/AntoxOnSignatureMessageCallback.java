package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactInfo;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.State;
import im.tox.tox4j.core.data.ToxStatusMessage;

/**
 * friend signature callback
 */
public class AntoxOnSignatureMessageCallback {
    public void friendStatusMessage(ContactInfo friendInfo, ToxStatusMessage message) {
        InfoRepository infoRepo = State.infoRepo();
        infoRepo.updateContactStatusMessage(friendInfo.getKey(), message);
    }
}
