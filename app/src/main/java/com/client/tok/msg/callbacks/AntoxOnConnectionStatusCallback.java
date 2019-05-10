package com.client.tok.msg.callbacks;

import com.client.tok.bean.ContactsKey;
import com.client.tok.db.repository.InfoRepository;
import com.client.tok.tox.ToxManager;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.enums.ToxConnection;

public class AntoxOnConnectionStatusCallback {
    private String TAG = "AntoxOnConnectionStatusCallback";

    public AntoxOnConnectionStatusCallback() {
    }

    /**
     * @param friendNumber The friend number of the friend whose connection status changed.
     * @param connectionStatus The new connection status.
     */
    public void friendConnectionStatus(ToxFriendNumber friendNumber,
        ToxConnection connectionStatus) {
        friendConnectionStatus(ToxManager.getManager().toxBase.getFriendKey(friendNumber),
            connectionStatus);
    }

    private void friendConnectionStatus(ContactsKey friendKey, ToxConnection connectionStatus) {
        boolean online = connectionStatus != ToxConnection.NONE;

        InfoRepository infoRepo = State.infoRepo();
        infoRepo.updateContactOnline(friendKey, online);
        LogUtil.i(TAG, "friend online:" + online + ",key:" + friendKey.getKey());
        if (online) {
            //MessageHelper.sendUnsentMessages(friendKey, ctx)
            State.transfers.updateSelfAvatar2Friend(friendKey.getKey());
        } //else {
        //ToxSingleton.typingMap().put(friendKey, false);
        //State.typing().onNext(true);
        //}
    }
}
