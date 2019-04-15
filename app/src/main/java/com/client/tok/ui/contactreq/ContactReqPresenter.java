package com.client.tok.ui.contactreq;

import com.client.tok.bean.FriendRequest;
import com.client.tok.tox.State;
import com.client.tok.utils.LogUtil;
import java.util.List;

public class ContactReqPresenter implements ContactReqContract.IContactReqPresenter {
    private String TAG = "ContactReqPresenter";
    private ContactReqContract.IContactReqView mContactReqView;

    public ContactReqPresenter(ContactReqContract.IContactReqView contactReqView) {
        this.mContactReqView = contactReqView;
        mContactReqView.setPresenter(this);
        start();
    }

    @Override
    public void start() {
        showContacts();
    }

    private void showContacts() {
        State.infoRepo()
            .friendReqLive()
            .observe(mContactReqView, (List<FriendRequest> friendReqList) -> {
                mContactReqView.showContactReq(friendReqList);
                State.infoRepo().setFriendReqRead();
            });
    }

    @Override
    public void onDestroy() {
        if (mContactReqView != null) {
            mContactReqView = null;
        }
        LogUtil.i(TAG, "contactReqPresenter onDestroy");
    }
}
