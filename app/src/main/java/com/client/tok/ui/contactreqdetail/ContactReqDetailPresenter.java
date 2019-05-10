package com.client.tok.ui.contactreqdetail;

import android.content.Intent;
import com.client.tok.R;
import com.client.tok.bean.FriendRequest;
import com.client.tok.notification.NotifyManager;
import com.client.tok.pagejump.IntentConstants;
import com.client.tok.tox.State;
import com.client.tok.ui.contactreq.ContactModel;
import com.client.tok.utils.StringUtils;
import com.client.tok.utils.ToastUtils;

public class ContactReqDetailPresenter
    implements ContactReqDetailContract.IContactReqDetailPresenter {
    private String TAG = "ContactReqDetailPresenter";
    private ContactReqDetailContract.IContactReqDetailView mContactReqDetailView;
    private ContactModel mContactModel;
    private Intent mIntent;
    private String mReqKey;
    private FriendRequest mFriendRequest;

    public ContactReqDetailPresenter(
        ContactReqDetailContract.IContactReqDetailView contactReqDetailView, Intent intent) {
        this.mContactReqDetailView = contactReqDetailView;
        mContactModel = new ContactModel();
        mIntent = intent;
        mContactReqDetailView.setPresenter(this);
        start();
    }

    public void start() {
        showContacts();
    }

    private void showContacts() {
        if (mIntent != null) {
            mReqKey = mIntent.getStringExtra(IntentConstants.REQ_FRIEND_KEY);
            mFriendRequest = State.infoRepo().getFriendReq(mReqKey);
            if (mFriendRequest != null) {
                mContactReqDetailView.showContactDetail(mFriendRequest);
            }
            State.infoRepo().setFriendReqRead(mReqKey);
            NotifyManager.getInstance().setBadge(State.infoRepo().totalUnreadCount());
        }
    }

    @Override
    public void acceptFriend() {
        if (mFriendRequest != null) {
            boolean accept = mContactModel.acceptNewContactRequest(mReqKey, "", "",
                StringUtils.getTextFromResId(R.string.friend_accepted_default_status));
            if (accept) {
                ToastUtils.show(R.string.successful);
                mContactReqDetailView.viewDestroy();
            } else {
                ToastUtils.show(R.string.fail_try_again);
            }
        }
    }

    @Override
    public void refuseFriend() {
        if (mFriendRequest != null) {
            boolean refuse = mContactModel.refuseNewContactRequest(mFriendRequest);
            if (refuse) {
                ToastUtils.show(R.string.successful);
                mContactReqDetailView.viewDestroy();
            } else {
                ToastUtils.show(R.string.fail_try_again);
            }
        }
    }

    public void onDestroy() {
        if (mContactReqDetailView != null) {
            mContactReqDetailView = null;
        }
    }
}
