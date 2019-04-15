package com.client.tok.ui.contactreq;

import com.client.tok.bean.ContactsKey;
import com.client.tok.bean.FriendRequest;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.GlobalParams;
import com.client.tok.tox.CoreManager;
import com.client.tok.tox.State;
import im.tox.tox4j.core.data.ToxNickname;
import im.tox.tox4j.core.enums.ToxMessageType;

public class ContactModel {
    public boolean acceptNewContactRequest(String key, String name, String alias,
        String statusMsg) {
        try {
            if (key != null) {
                ContactsKey contactsKey = new ContactsKey(key);
                InfoRepository db = State.infoRepo();
                db.addFriend(contactsKey, name, alias, statusMsg);
                db.delFriendRequest(key);
                NotifyManager.getInstance().cleanNotify(key.hashCode());
                CoreManager.getManager().toxBase.acceptAddFriendRequest(contactsKey);
                CoreManager.getManager().save();

                //add conversation
                State.infoRepo().addConversation(key);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addHelloMsg(FriendRequest friendRequest) {
        State.infoRepo()
            .addMessage2(friendRequest.getRequestKey(), friendRequest.getRequestKey(),
                ToxNickname.unsafeFromValue("".getBytes()), friendRequest.getRequestMessage(), 0L,
                GlobalParams.SEND_SUCCESS, true, ToxMessageType.NORMAL, -1);
    }

    public boolean refuseNewContactRequest(FriendRequest friendRequest) {
        if (friendRequest != null) {
            InfoRepository db = State.infoRepo();
            ContactsKey key = friendRequest.getRequestKey();
            db.delFriendRequest(key.key);
            return true;
        }
        return false;
    }
}
